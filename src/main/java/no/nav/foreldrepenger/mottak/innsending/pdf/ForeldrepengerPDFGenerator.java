package no.nav.foreldrepenger.mottak.innsending.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Opptjening;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.RelasjonTilBarnMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Rettigheter;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@Component
public class ForeldrepengerPDFGenerator implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerPDFGenerator.class);
    private final Oppslag oppslag;
    private final ForeldrepengeInfoRenderer fpRenderer;

    private final PDFElementRenderer pdfRenderer;
    private Environment env;

    @Inject
    public ForeldrepengerPDFGenerator(@Qualifier("landkoder") MessageSource landkoder,
            @Qualifier("kvitteringstekster") MessageSource kvitteringstekster,
            Oppslag oppslag) {
        this(oppslag, new PDFElementRenderer(), new ForeldrepengeInfoRenderer(landkoder, kvitteringstekster));
    }

    private ForeldrepengerPDFGenerator(Oppslag oppslag, PDFElementRenderer pdfRenderer,
            ForeldrepengeInfoRenderer fpRenderer) {
        this.oppslag = oppslag;
        this.pdfRenderer = pdfRenderer;
        this.fpRenderer = fpRenderer;
    }

    public byte[] generate(Søknad søknad, Person søker) {
        return generate(søknad, søker, Collections.emptyList());
    }

    public byte[] generate(Søknad søknad, Person søker, final List<Arbeidsforhold> arbeidsforhold) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = pdfRenderer.newPage();
            doc.addPage(page);
            try {
                PDPageContentStream cos = new PDPageContentStream(doc, page);
                float y = yTop;
                LOG.info("Y ved start {}", y);
                y = fpRenderer.header(søker, doc, cos, false, y);
                float headerSize = yTop - y;
                LOG.info("Heaader trenger  {}", headerSize);
                LOG.info("Y før annen  forelder {}", y);
                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y = fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                            stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }
                LOG.info("Y før dekninggsgrad {}", y);
                if (stønad.getDekningsgrad() != null) {
                    y = fpRenderer.dekningsgrad(stønad.getDekningsgrad(), cos, y);
                }

                Opptjening opptjening = stønad.getOpptjening();
                List<Arbeidsforhold> faktiskearbeidsforhold = arbeidsforhold(arbeidsforhold);
                if (opptjening != null) {
                    LOG.info("Y før opptjening {}", y);
                    PDPage scratch = pdfRenderer.newPage();
                    PDPageContentStream scratchcos = new PDPageContentStream(doc, scratch);
                    float startY = PDFElementRenderer.calculateStartY();
                    startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                    float size = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, scratchcos, startY);
                    float behov = startY - size;
                    if (behov <= y) {
                        scratchcos.close();
                        LOG.info("Nok plass til opptjening, trenger {}, har {}", behov, y);
                        y = fpRenderer.arbeidsforholdOpptjening(faktiskearbeidsforhold, cos, y);
                    }
                    else {
                        LOG.info("Trenger ny side. IKKE nok plass til opptjening på denne siden, trenger {}, har {}",
                                behov, y);
                        cos.close();
                        doc.addPage(scratch);
                        cos = scratchcos;
                        y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                    }
                    if (!opptjening.getUtenlandskArbeidsforhold().isEmpty()) {
                        LOG.info("Y før utenlandsk arbeidsforhold {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                                opptjening.getUtenlandskArbeidsforhold(),
                                søknad.getVedlegg(),
                                scratchcos, startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til utenlandsk arbeidsforhold, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.utenlandskeArbeidsforholdOpptjening(
                                    opptjening.getUtenlandskArbeidsforhold(),
                                    søknad.getVedlegg(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til utenlandsk arbeidsforhold på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (!opptjening.getAnnenOpptjening().isEmpty()) {
                        LOG.info("Y før annen opptjening {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.annenOpptjening(opptjening.getAnnenOpptjening(), søknad.getVedlegg(),
                                scratchcos, startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til annen opptjening, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.annenOpptjening(
                                    opptjening.getAnnenOpptjening(),
                                    søknad.getVedlegg(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til annen opptjening på denne siden, trenger {}, har {}",
                                    behov,
                                    y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (!opptjening.getEgenNæring().isEmpty()) {
                        LOG.info("Y før egen næring {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), scratchcos, startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til egen næring, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.egneNæringerOpptjening(opptjening.getEgenNæring(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til egen næring på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (opptjening.getFrilans() != null) {
                        LOG.info("Y før frilans {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.frilansOpptjening(opptjening.getFrilans(),
                                scratchcos, startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til frilans, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.frilansOpptjening(opptjening.getFrilans(), cos, y);
                        }
                        else {
                            LOG.info("Trenger ny side. IKKE nok plass til frilans på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (stønad.getMedlemsskap() != null) {
                        LOG.info("Y før medlemsskap {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.medlemsskap(stønad.getMedlemsskap(), scratchcos, startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til medlemsskap, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.medlemsskap(stønad.getMedlemsskap(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til medlemsskap på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (stønad.getRelasjonTilBarn() != null) {
                        LOG.info("Y før relasjon til barn {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), scratchcos,
                                startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til relasjon til barn, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.relasjonTilBarn(stønad.getRelasjonTilBarn(), søknad.getVedlegg(), cos, y);
                        }
                        else {
                            LOG.info("Trenger ny side. IKKE nok plass til relasjon på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (stønad.getFordeling() != null) {
                        LOG.info("Y før fordeling {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.fordeling(stønad.getFordeling(), scratchcos,
                                startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til fordeling, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.fordeling(stønad.getFordeling(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til fordeling på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                    if (stønad.getRettigheter() != null) {
                        LOG.info("Y før rettigheter {}", y);
                        PDPage scratch1 = pdfRenderer.newPage();
                        scratchcos = new PDPageContentStream(doc, scratch1);
                        startY = PDFElementRenderer.calculateStartY();
                        startY = fpRenderer.header(søker, doc, scratchcos, false, startY);
                        size = fpRenderer.rettigheter(stønad.getRettigheter(), scratchcos,
                                startY);
                        behov = startY - size;
                        if (behov <= y) {
                            LOG.info("Nok plass til rettigheter, trenger {}, har {}", behov, y);
                            scratchcos.close();
                            y = fpRenderer.rettigheter(stønad.getRettigheter(), cos, y);
                        }
                        else {
                            LOG.info(
                                    "Trenger ny side. IKKE nok plass til rettigheter på denne siden, trenger {}, har {}",
                                    behov, y);
                            cos.close();
                            doc.addPage(scratch1);
                            cos = scratchcos;
                            y = PDFElementRenderer.calculateStartY() - behov - headerSize;
                        }
                    }

                }
                cos.close();
                doc.save(baos);
                return baos.toByteArray();
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    public byte[] generate(Endringssøknad søknad, Person søker) {
        Foreldrepenger stønad = Foreldrepenger.class.cast(søknad.getYtelse());
        float yTop = PDFElementRenderer.calculateStartY();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = pdfRenderer.newPage();
            try (PDPageContentStream cos = new PDPageContentStream(doc, page)) {
                float y = yTop;
                y = fpRenderer.header(søker, doc, cos, true, y);
                AnnenForelder annenForelder = stønad.getAnnenForelder();
                if (annenForelder != null) {
                    y -= fpRenderer.annenForelder(annenForelder, stønad.getFordeling().isErAnnenForelderInformert(),
                            stønad.getRettigheter().isHarAnnenForelderRett(), cos,
                            y);
                }

                RelasjonTilBarnMedVedlegg relasjon = stønad.getRelasjonTilBarn();
                if (relasjon != null) {
                    y -= fpRenderer.relasjonTilBarn(relasjon, søknad.getVedlegg(), cos, y);
                }

                Fordeling fordeling = stønad.getFordeling();
                if (fordeling != null) {
                    y -= fpRenderer.fordeling(fordeling, cos, y);
                }

                Rettigheter rettigheter = stønad.getRettigheter();
                if (rettigheter != null) {
                    y -= fpRenderer.rettigheter(rettigheter, cos, y);
                }
                final List<Vedlegg> vedlegg = søknad.getVedlegg();
                if (vedlegg != null && !vedlegg.isEmpty()) {
                    fpRenderer.vedlegg(søknad.getVedlegg(), cos, y);
                }
                doc.addPage(page);
            } catch (IOException ex) {
                throw new RuntimeException("Error while creating pdf", ex);
            }
            doc.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Error while creating pdf", ex);
        }
    }

    private List<Arbeidsforhold> arbeidsforhold(List<Arbeidsforhold> forhold) {
        return forhold.isEmpty() ? oppslag.getArbeidsforhold() : forhold;

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [oppslag=" + oppslag + ", fpRenderer=" + fpRenderer + ", pdfRenderer="
                + pdfRenderer + ", env=" + env + "]";
    }
}
