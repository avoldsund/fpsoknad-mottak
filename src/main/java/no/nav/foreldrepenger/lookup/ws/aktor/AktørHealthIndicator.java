package no.nav.foreldrepenger.lookup.ws.aktor;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.EnvironmentAwareServiceHealthIndicator;

@Component
public class AktørHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final AktorIdClient client;

    public AktørHealthIndicator(AktorIdClient client, @Value("${AKTOER_V2_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}
