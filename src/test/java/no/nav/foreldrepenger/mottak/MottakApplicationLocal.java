package no.nav.foreldrepenger.mottak;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.http.AktørIDLookup;
import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableOIDCTokenValidation(ignore = { "org.springframework" })
@SpringBootApplication
@Import(value = TokenGeneratorConfiguration.class)
@EnableSwagger2
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = MottakApplication.class) })
public class MottakApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(MottakApplicationLocal.class, args);
    }

    @Bean
    @Primary
    public AktørIDLookup aktørService() {
        return new AktørIDLookup() {

            @Override
            public AktorId getAktørId() {
                return new AktorId("42");
            }
        };
    }
}