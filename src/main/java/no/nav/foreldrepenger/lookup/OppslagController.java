package no.nav.foreldrepenger.lookup;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoFagsakYtelseType;
import no.nav.foreldrepenger.lookup.rest.fpinfo.FPInfoSakStatus;
import no.nav.foreldrepenger.lookup.rest.fpinfo.SaksStatusService;
import no.nav.foreldrepenger.lookup.ws.Søkerinfo;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.person.ID;
import no.nav.foreldrepenger.lookup.ws.person.Person;
import no.nav.foreldrepenger.lookup.ws.person.PersonClient;
import no.nav.security.oidc.api.Unprotected;
import no.nav.security.oidc.context.OIDCRequestContextHolder;

@RestController
@no.nav.security.oidc.api.ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping("/oppslag")
public class OppslagController {

    private static final Logger LOG = getLogger(OppslagController.class);

    private final AktorIdClient aktorClient;

    private final PersonClient personClient;

    private final ArbeidsforholdClient arbeidsforholdClient;

    private final SaksStatusService saksStatusService;

    private final OIDCRequestContextHolder contextHolder;

    @Inject
    public OppslagController(AktorIdClient aktorClient, PersonClient personClient,
            ArbeidsforholdClient arbeidsforholdClient, SaksStatusService saksStatusService,
            OIDCRequestContextHolder contextHolder) {
        this.aktorClient = aktorClient;
        this.personClient = personClient;
        this.arbeidsforholdClient = arbeidsforholdClient;
        this.saksStatusService = saksStatusService;
        this.contextHolder = contextHolder;
    }

    @Unprotected
    @GetMapping(value = "/ping", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ping(
            @RequestParam(name = "register", defaultValue = "all", required = false) Pingable register) {
        LOG.info("Vil pinge register {}", register);
        switch (register) {
        case aareg:
            arbeidsforholdClient.ping();
            break;
        case aktør:
            aktorClient.ping();
            break;
        case tps:
            personClient.ping();
            break;
        case all:
            aktorClient.ping();
            personClient.ping();
            arbeidsforholdClient.ping();
            break;
        }
        return ok(registerNavn(register) + " er i toppform");
    }

    @GetMapping
    public ResponseEntity<Søkerinfo> essensiellSøkerinfo() {
        Fødselsnummer fnr = fnrFromClaims();
        Person person = personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
        List<Arbeidsforhold> arbeidsforhold = arbeidsforholdClient.arbeidsforhold(fnr);
        return ok(new Søkerinfo(person, arbeidsforhold));
    }

    @GetMapping(value = "/saker", produces = APPLICATION_JSON_VALUE)
    public List<FPInfoSakStatus> saker() {
        return saksStatusService.hentSaker(id(), FPInfoFagsakYtelseType.FP);
    }

    private String id() {
        return aktorClient.aktorIdForFnr(fnrFromClaims()).getAktør();
    }

    @GetMapping(value = "/aktor")
    public AktorId getAktørId() {
        return getAktørIdForFNR(fnrFromClaims());
    }

    @GetMapping(value = "/aktorfnr")
    public AktorId getAktørIdForFNR(@RequestParam(name = "fnr") Fødselsnummer fnr) {
        return aktorClient.aktorIdForFnr(fnr);
    }

    private Fødselsnummer fnrFromClaims() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().isEmpty()) {
            throw new ForbiddenException("Fant ikke FNR i token");
        }
        return new Fødselsnummer(fnrFromClaims);
    }

    private static String registerNavn(Pingable register) {
        return register.equals(Pingable.all)
                ? Arrays.stream(Pingable.values())
                        .map(Pingable::name)
                        .filter(s -> s != "all")
                        .collect(Collectors.joining(","))
                : register.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient
                + ", aaregClient=" + arbeidsforholdClient + ", contextHolder=" + contextHolder + "]";
    }

}