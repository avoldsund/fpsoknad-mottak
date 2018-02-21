package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.domain.Pair;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;

@Service
public class DokmotJMSSender implements SøknadSender {

    private final JmsTemplate dokmotTemplate;
    private final DokmotEngangsstønadXMLKonvoluttGenerator generator;
    private final CallIdGenerator callIdGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(DokmotJMSSender.class);

    @Inject
    public DokmotJMSSender(JmsTemplate template, DokmotEngangsstønadXMLKonvoluttGenerator generator,
            CallIdGenerator callIdGenerator) {
        this.dokmotTemplate = template;
        this.generator = generator;
        this.callIdGenerator = callIdGenerator;
    }

    @Override
    public SøknadSendingsResultat sendSøknad(Søknad søknad) {
        String xml = generator.toXML(søknad);
        try {
            dokmotTemplate.send(session -> {
                Pair<String, String> callId = callIdGenerator.generateCallId();
                LOG.info("Sending message to DOKMOT {}", xml);
                TextMessage msg = session.createTextMessage(xml);
                msg.setStringProperty("callId", callId.getSecond());
                return msg;
            });
            return SøknadSendingsResultat.OK;
        } catch (JmsException e) {
            LOG.warn("Unable to send to DOKMOT", e);
            throw (e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [dokmotTemplate=" + dokmotTemplate + ", generator=" + generator + "]";
    }

}
