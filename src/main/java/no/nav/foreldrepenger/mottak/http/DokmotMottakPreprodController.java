package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotEngangsstønadXMLKonvoluttGenerator;
import no.nav.foreldrepenger.mottak.dokmot.DokmotJMSSender;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.soeknadsskjema.engangsstoenad.v1.SoeknadsskjemaEngangsstoenad;

@RestController
@RequestMapping(value = DokmotMottakPreprodController.DOKMOT_PREPROD, produces = APPLICATION_XML_VALUE)
@Profile("preprod")
public class DokmotMottakPreprodController {

    public static final String DOKMOT_PREPROD = "/mottak/preprod";

    private final DokmotJMSSender sender;
    private final DokmotEngangsstønadXMLGenerator søknadGenerator;
    private final DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator;

    public DokmotMottakPreprodController(DokmotJMSSender sender, DokmotEngangsstønadXMLGenerator søknadGenerator,
            DokmotEngangsstønadXMLKonvoluttGenerator konvoluttGenerator) {
        this.sender = sender;
        this.søknadGenerator = søknadGenerator;
        this.konvoluttGenerator = konvoluttGenerator;
    }

    @PostMapping("/søknad")
    public ResponseEntity<String> søknad(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknadGenerator.toXML(søknad));
    }

    @PostMapping("/konvolutt")
    public ResponseEntity<String> konvolutt(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(konvoluttGenerator.toXML(søknad));
    }

    @PostMapping(value = "/model", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SoeknadsskjemaEngangsstoenad> dokmotmodel(@Valid @RequestBody Søknad søknad) {
        return ResponseEntity.status(HttpStatus.OK).body(søknadGenerator.toDokmotModel(søknad));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sender=" + sender + ", søknadGenerator=" + søknadGenerator
                + ", konvoluttGenerator=" + konvoluttGenerator + "]";
    }

}
