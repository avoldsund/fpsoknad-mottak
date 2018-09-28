package no.nav.foreldrepenger.mottak.http;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.util.FnrExtractor;
import no.nav.foreldrepenger.mottak.util.MDCUtil;

@Component
@Order(1)
public class CallAndConsumerIdFilter extends GenericFilterBean {

    private static final String FPSOKNAD_MOTTAK = "fpsoknad-mottak";

    private static final String USER_ID = "Nav-User-Id";

    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    private final CallIdGenerator generator;

    private final FnrExtractor extractor;

    @Inject
    public CallAndConsumerIdFilter(CallIdGenerator generator, FnrExtractor extractor) {
        this.generator = generator;
        this.extractor = extractor;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        propagateOrCreate(request);
        chain.doFilter(request, response);
    }

    private void propagateOrCreate(ServletRequest request) {
        HttpServletRequest req = HttpServletRequest.class.cast(request);
        propagateOrCreate(NAV_CONSUMER_ID, req, FPSOKNAD_MOTTAK);
        propagateOrCreate(generator.getCallIdKey(), req, generator.create());
        MDC.put(USER_ID, extractor.fnrFromToken());
    }

    private static void propagateOrCreate(String key, HttpServletRequest req, String defaultValue) {
        MDCUtil.put(key, Optional.ofNullable(req.getHeader(key)).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", extractor=" + extractor + "]";
    }

}