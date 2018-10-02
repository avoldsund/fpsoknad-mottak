package no.nav.foreldrepenger.mottak.http.health;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.http.OppslagConnection;

@Component
public class OppslagHealthIndicator extends EnvironmentAwareHealthIndicator {

    public OppslagHealthIndicator(OppslagConnection connection) {
        super(connection);
    }
}
