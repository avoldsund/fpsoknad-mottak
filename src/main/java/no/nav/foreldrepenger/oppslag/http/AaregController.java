package no.nav.foreldrepenger.oppslag.http;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.http.util.FnrExtractor;
import no.nav.security.oidc.filter.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class AaregController {

    @Inject
    private AaregClient aaregClient;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @RequestMapping(method = { RequestMethod.GET }, value = "/aareg")
    public ResponseEntity<List<Arbeidsforhold>> workHistory() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        List<Arbeidsforhold> arbeidsforhold = aaregClient.arbeidsforhold(fnr);
        return ResponseEntity.ok(arbeidsforhold);
    }
}