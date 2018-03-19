package no.nav.foreldrepenger.oppslag.http;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import no.nav.security.spring.oidc.validation.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;

@RestController
@Validated
class FpsakController {

    private final FpsakClient fpsakClient;

    @Inject
    public FpsakController(FpsakClient fpsakClient) {
        this.fpsakClient = fpsakClient;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/fpsak")
    @Protected
    public ResponseEntity<List<Ytelse>> existingCases(@Valid @RequestParam("aktør") AktorId aktor) {
        return ResponseEntity.ok(fpsakClient.casesFor(aktor));
    }
}
