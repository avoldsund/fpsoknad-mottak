package no.nav.foreldrepenger.mottak.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MottakConfiguration {

    public static final String LANDKODER = "landkoder";
    public static final String KVITTERINGSTEKSTER = "kvitteringstekster";

    @Bean
    @Qualifier(LANDKODER)
    public MessageSource landkoder() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(LANDKODER);
        return messageSource;
    }

    @Bean
    @Qualifier(KVITTERINGSTEKSTER)
    public MessageSource kvitteringstekster() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(KVITTERINGSTEKSTER);
        return messageSource;
    }
}
