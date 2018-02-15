package no.nav.foreldrepenger.mottak.dokmot;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class DokmotQueueHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(DokmotQueueHealthIndicator.class);

    private final JmsTemplate dokmotTemplate;

    @Inject
    public DokmotQueueHealthIndicator(JmsTemplate dokmotTemplate) {
        this.dokmotTemplate = dokmotTemplate;
    }

    @Override
    public Health health() {
        try {
            dokmotTemplate.getConnectionFactory().createConnection().close();
            return Health.up().build();
        } catch (Exception e) {
            LOG.warn("Could not verify health of queue", e);
            return Health.down().build();
        }
    }

}
