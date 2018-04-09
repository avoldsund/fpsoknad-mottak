package no.nav.foreldrepenger.mottak.http;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSendingsResultat;
import no.nav.security.spring.oidc.validation.api.Unprotected;

@RestController

@RequestMapping(path = DokmotMottakController.DOKMOT, produces = APPLICATION_JSON_VALUE)
@Unprotected
// @ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class DokmotMottakController {

    private static final Logger LOG = getLogger(DokmotMottakController.class);

    public static final String DOKMOT = "/mottak/dokmot";

    private final DokmotJMSSender sender;
    @Autowired
    DokmotEngangsstønadXMLGenerator søknadGenerator;
    @Autowired
    DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    @Inject
    public DokmotMottakController(DokmotJMSSender sender) {
        this.sender = sender;
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public ResponseEntity<String> ping(@RequestParam("navn") String navn) {
        LOG.info("I was pinged");
        return ResponseEntity.status(HttpStatus.OK).body("Hello " + navn + " from unprotected resource");
    }

    @GetMapping(value = "/ping1")
    public ResponseEntity<String> ping1(@RequestParam("navn") String navn) {
        LOG.info("I was pinged");
        return ResponseEntity.status(HttpStatus.OK).body("Hello " + navn + " from protected resource");
    }

    @PostMapping(value = "/send")
    public ResponseEntity<SøknadSendingsResultat> send(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(sender.sendSøknad(søknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + "]";
    }
}