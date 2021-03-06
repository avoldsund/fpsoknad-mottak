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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "varsel.enabled", havingValue = "true")
public class VarselJMSConnection implements VarselConnection {

    private static final Logger LOG = LoggerFactory.getLogger(VarselJMSConnection.class);

    private final JmsTemplate template;
    private final VarselConfig varselConfig;

    public VarselJMSConnection(JmsTemplate template, VarselConfig varselConfig) {
        this.template = template;
        this.varselConfig = varselConfig;
    }

    @Override
    public String ping() {
        LOG.info("Pinger {} ({})", name(), varselConfig.getURI());
        try {
            template.getConnectionFactory().createConnection().close();
            return name() + " er i live på " + pingEndpoint();
        } catch (JMSException e) {
            LOG.warn("Kunne ikke pinge {}-kø ({})", name(), varselConfig.getURI(), e);
            throw new IllegalArgumentException("Kunne ikke pinge " + name() + "-kø", e);
        }
    }

    @Override
    public URI pingEndpoint() {
        return varselConfig.getURI();
    }

    private boolean isEnabled() {
        return getConfig().isEnabled();
    }

    @Override
    public String name() {
        return "varseltjeneste";
    }

    @Override
    public void varsle(String xml) {
        if (isEnabled()) {
            LOG.info("Legger melding for varsel på {}-kø ({})", name(), varselConfig.getURI());
            try {
                template.send(session -> {
                    TextMessage msg = session.createTextMessage(xml);
                    msg.setStringProperty(CALL_ID, callId());
                    return msg;
                });
                VARSEL_SUCCESS.increment();
            } catch (JmsException swallow) {
                LOG.error("Feil ved sending av varsel til {}-kø ({})", name(), varselConfig.getURI(), swallow);
                VARSEL_FAILED.increment();
            }
        } else {
            LOG.info("Varsling er deaktivert");
        }

    }

    VarselConfig getConfig() {
        return varselConfig;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", varselConfig=" + varselConfig.getURI() + "]";
    }

}
