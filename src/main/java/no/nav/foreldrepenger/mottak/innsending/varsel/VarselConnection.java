package no.nav.foreldrepenger.mottak.innsending.varsel;

import static no.nav.foreldrepenger.mottak.Constants.CALL_ID;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_FAILED;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.VARSEL_SUCCESS;
import static no.nav.foreldrepenger.mottak.util.MDCUtil.callId;

import java.net.URI;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.PingEndpointAware;

@Component
public class VarselConnection implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(VarselConnection.class);

    private final JmsTemplate template;
    private final VarselQueueConfig queueConfig;

    public VarselConnection(JmsTemplate template, VarselQueueConfig queueConfig) {
        this.template = template;
        this.queueConfig = queueConfig;
    }

    @Override
    public String ping() {
        LOG.info("Pinger {} ({})", name(), queueConfig.getURI());
        try {
            template.getConnectionFactory().createConnection().close();
            return ("Alive and kicking");
        } catch (JMSException e) {
            LOG.warn("Kunne ikke pinge {}-kø ({})", name(), queueConfig.getURI(), e);
            throw new IllegalArgumentException("Kunne ikke pinge " + name() + "-kø", e);
        }
    }

    @Override
    public URI pingEndpoint() {
        return queueConfig.getURI();
    }

    @Override
    public String name() {
        return "varseltjeneste";
    }

    void send(String xml) {
        send(session -> {
            TextMessage msg = session.createTextMessage(xml);
            msg.setStringProperty(CALL_ID, callId());
            return msg;
        });
    }

    private void send(MessageCreator msg) {
        try {
            LOG.info("Legger melding på {}-kø ({})", name(), queueConfig.getURI());
            template.send(msg);
            VARSEL_SUCCESS.increment();
        } catch (JmsException swallow) {
            LOG.error("Feil ved sending til {}-kø ({})", name(), queueConfig.getURI(), swallow);
            VARSEL_FAILED.increment();
        }
    }

    public VarselQueueConfig getQueueConfig() {
        return queueConfig;
    }

    public boolean isEnabled() {
        return getQueueConfig().isEnabled();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", queueConfig=" + queueConfig.getURI() + "]";
    }

}
