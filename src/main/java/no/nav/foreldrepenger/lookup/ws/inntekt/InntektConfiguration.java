package no.nav.foreldrepenger.lookup.ws.inntekt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;

@Configuration
public class InntektConfiguration extends WsClient<InntektV3> {

    @Bean
    @Qualifier("inntektV3")
    public InntektV3 inntektV3(@Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, InntektV3.class);
    }

    @Bean
    @Qualifier("healthIndicatorInntekt")
    public InntektV3 healthIndicatorInntekt(@Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, InntektV3.class);
    }

    @Bean
    public InntektClient inntektClientWs(@Qualifier("inntektV3") InntektV3 inntektV3,
            @Qualifier("healthIndicatorInntekt") InntektV3 healthIndicator) {
        return new InntektClientWs(inntektV3, healthIndicator);
    }
}
