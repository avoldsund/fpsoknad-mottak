package no.nav.foreldrepenger.oppslag.aareg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class AaregConfiguration extends WsClient<ArbeidsforholdV3>{

    @SuppressWarnings("unchecked")
    @Bean
    public ArbeidsforholdV3 arbeidsforholdV3(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, ArbeidsforholdV3.class);
    }
}