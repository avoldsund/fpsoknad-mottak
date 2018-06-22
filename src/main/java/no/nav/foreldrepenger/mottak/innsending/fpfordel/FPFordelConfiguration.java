package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FPFordelConfiguration {

    private static final class NonRedirectingRequestFactory extends HttpComponentsClientHttpRequestFactory {

        private static final Logger LOG = LoggerFactory.getLogger(NonRedirectingRequestFactory.class);

        public NonRedirectingRequestFactory() {
            setHttpClient(HttpClientBuilder.create()
                    .setRedirectStrategy(new DefaultRedirectStrategy() {

                        @Override
                        protected boolean isRedirectable(String method) {
                            LOG.info("Method {} is not redirectable", method);
                            return false;
                        }

                    })
                    .build());
        }
    }

    @Bean
    public RestTemplate restTemplate(FPFordelConfig cfg, ClientHttpRequestInterceptor... interceptors) {

        RestTemplate template = new RestTemplateBuilder()
                .rootUri(cfg.getUri())
                .requestFactory(NonRedirectingRequestFactory.class)
                .interceptors(interceptors)
                .errorHandler(new FPFordeResponseErrorHandler())
                .build();
        template.getMessageConverters().add(new MultipartMixedAwareMessageConverter());
        return template;
    }

}
