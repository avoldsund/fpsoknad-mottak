package no.nav.foreldrepenger.mottak.http.filters;

import static no.nav.foreldrepenger.mottak.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.mottak.Constants.NAV_TOKEN_EXPIRY_ID;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.mottak.domain.CallIdGenerator;
import no.nav.foreldrepenger.mottak.util.TokenUtil;

@Component
@Order(HIGHEST_PRECEDENCE)
public class HeadersToMDCFilterBean extends GenericFilterBean {

    private final CallIdGenerator generator;
    private final String applicationName;
    private final TokenUtil tokenUtil;

    @Inject
    public HeadersToMDCFilterBean(CallIdGenerator generator, TokenUtil tokenUtil,
            @Value("${spring.application.name}") String applicationName) {
        this.generator = generator;
        this.tokenUtil = tokenUtil;
        this.applicationName = applicationName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putValues(HttpServletRequest.class.cast(request));
        chain.doFilter(request, response);
    }

    private void putValues(HttpServletRequest request) {
        putValue(NAV_CONSUMER_ID, request.getHeader(NAV_CONSUMER_ID), applicationName);
        putValue(NAV_CALL_ID, request.getHeader(NAV_CALL_ID), generator.create());
        if (tokenUtil.getExpiryDate() != null) {
            putValue(NAV_TOKEN_EXPIRY_ID, tokenUtil.getExpiryDate().toString(), null);
        }
    }

    private static void putValue(String key, String value, String defaultValue) {
        MDC.put(key, Optional.ofNullable(value).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}
