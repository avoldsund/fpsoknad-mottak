package no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.fpsak;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.oppslag.lookup.ws.ytelser.Ytelse;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@Validated
@ProtectedWithClaims(issuer="selvbetjening", claimMap={"acr=Level4"})
class FpsakController {

    private final FpsakClient fpsakClient;

    @Inject
    public FpsakController(FpsakClient fpsakClient) {
        this.fpsakClient = fpsakClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/fpsak")
    public ResponseEntity<List<Ytelse>> existingCases(@Valid @RequestParam("aktør") AktorId aktor) {
        return ResponseEntity.ok(fpsakClient.casesFor(aktor));
    }
}
