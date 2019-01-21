package no.nav.foreldrepenger.mottak.util.jaxb;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.CONFIDENTIAL;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.mottak.util.Versjon;

public abstract class AbstractJAXBUtil {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractJAXBUtil.class);

    private final JAXBContext context;
    private final Schema schema;
    private final boolean validateMarshalling;
    private final boolean validateUnarshalling;

    protected static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

    public AbstractJAXBUtil(JAXBContext context, Versjon versjon, boolean validateMarhsalling,
            boolean validateUnmarshalling, String... xsds) {
        this.context = context;
        this.schema = schema(versjon, xsds);
        this.validateMarshalling = validateMarhsalling;
        this.validateUnarshalling = validateUnmarshalling;
    }

//    public AbstractJAXBUtil(JAXBContext context, String xsd) {
//        this.context = context;
//        this.schema = schema(xsd);
//    }

    protected static JAXBContext contextFra(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt med konfigurasjon av kontekst fra {}", Arrays.toString(classes), e);
            throw new IllegalArgumentException(e);
        }
    }

    public Element marshalToElement(Object model) {
        try {
            DOMResult res = new DOMResult();
            marshaller().marshal(model, res);
            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt ved marshalling til element av model {}", model.getClass(), e);
            throw new IllegalArgumentException(e);
        }
    }

    public String marshal(Object model) {
        try {
            StringWriter sw = new StringWriter();
            marshaller().marshal(model, sw);
            return sw.toString();
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt ved marshalling av model {}", model.getClass(), e);
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T unmarshal(byte[] bytes, Class<T> clazz) {
        return unmarshal(new String(bytes), clazz);
    }

    public <T> T unmarshal(String xml, Class<T> clazz) {
        try {
            return (T) unmarshaller().unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt ved unmarshalling til klasse {}", clazz.getName(), e);
            LOG.warn(CONFIDENTIAL, "XML er {}", xml);
            throw new IllegalArgumentException(e);
        }
    }

    public <T> JAXBElement<T> unmarshalToElement(String xml, Class<T> clazz) {
        try {
            return (JAXBElement<T>) unmarshaller().unmarshal(new StringReader(unescapeHtml4(xml)));
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt ved unmarshalling til klasse {}", clazz.getName(), e);
            LOG.warn(CONFIDENTIAL, "XML er {}", xml);
            throw new IllegalArgumentException(e);
        }
    }

    private static Schema schema(Versjon versjon, String... xsds) {
        try {
            return SCHEMA_FACTORY.newSchema(sourcesFra(versjon, xsds));
        } catch (SAXException e) {
            LOG.warn(
                    "Noe gikk galt med konfigurasjon av skjema for validering fra {}, bruker ikke-validerende marshaller",
                    Arrays.toString(xsds), e);
            return null;
        }
    }

    private static Schema schema(String xsd) {
        try {
            return SCHEMA_FACTORY.newSchema(sourceFra(xsd));
        } catch (SAXException e) {
            LOG.warn(
                "Noe gikk galt med konfigurasjon av skjema for validering fra {}, bruker ikke-validerende marshaller",
                xsd, e);
            return null;
        }
    }

    private Unmarshaller unmarshaller() {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            if (schema != null && validateUnarshalling) {
                unmarshaller.setSchema(schema);
            }
            return unmarshaller;
        } catch (JAXBException e) {
            LOG.warn("Noe gikk galt ved konstruksjon av unmarshaller fra kontekst {} og skjema {}", context, schema, e);
            throw new IllegalArgumentException(e);
        }
    }

    private Marshaller marshaller() {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.setEventHandler(new DefaultValidationEventHandler());
            if (schema != null && validateMarshalling) {
                marshaller.setSchema(schema);
            }
            return marshaller;
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved konstruksjon av marshaller fra kontekst {} ", context, e);
            throw new IllegalArgumentException(e);
        }
    }

    private static Source[] sourcesFra(Versjon version, String... schemas) {
        return Arrays.stream(schemas)
                .map(s -> version.name().toLowerCase() + s)
                .map(AbstractJAXBUtil::sourceFra)
                .toArray(Source[]::new);

    }

    private static Source sourceFra(String re) {
        try {
            URL url = AbstractJAXBUtil.class.getClassLoader().getResource(re);
            return new StreamSource(inputStreamFra(new UrlResource(url)), url.toExternalForm());
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved lesing av ressurs {} fra classpath", re, e);
            throw new IllegalArgumentException(e);
        }
    }

    private static InputStream inputStreamFra(UrlResource res) {
        try {
            if (!res.exists()) {
                throw new IllegalArgumentException("Ressursen  " + res + " finnes ikke");
            }
            return res.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("Ingen input stream for " + res, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [context=" + context + ", schema=" + schema + "]";
    }

}
