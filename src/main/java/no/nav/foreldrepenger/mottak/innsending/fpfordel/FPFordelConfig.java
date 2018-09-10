package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fpfordel")
@Configuration
public class FPFordelConfig {

    boolean enabled;
    String uri;

    public String getUri() {
        return uri != null ? uri : "http://fpfordel";
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [enabled=" + enabled + ", uri=" + uri + "]";
    }

}