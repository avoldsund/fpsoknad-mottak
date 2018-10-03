package no.nav.foreldrepenger.mottak.http.filters;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.http.InnsynController.INNSYN;
import static no.nav.foreldrepenger.mottak.http.SøknadController.INNSENDING;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.mottak.util.EnvUtil.PREPROD;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({ PREPROD, DEV })
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {

    public IDFilterRegistrationBean(IDToMDCFilterBean idFiltr) {
        setFilter(idFiltr);
        setUrlPatterns(newArrayList(INNSENDING + "/*", INNSYN + "/*"));
    }
}