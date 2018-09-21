package no.nav.foreldrepenger.mottak.http;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.SøknadSender;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.EndringsSøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.InnsynTjeneste;
import no.nav.foreldrepenger.mottak.innsending.fpinfo.SakStatus;
import no.nav.foreldrepenger.mottak.util.EnvUtil;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RestController
@RequestMapping(path = SøknadController.MOTTAK, produces = APPLICATION_JSON_VALUE)
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SøknadController {

    private static final Logger LOG = LoggerFactory.getLogger(SøknadController.class);

    @Inject
    Environment env;

    public static final String MOTTAK = "/mottak";

    private final InnsynTjeneste innsynTjeneste;
    private final Oppslag oppslag;
    private final SøknadSender sender;

    public SøknadController(@Qualifier("dual") SøknadSender sender, Oppslag oppslag, InnsynTjeneste innsynTjeneste) {
        this.sender = sender;
        this.oppslag = oppslag;
        this.innsynTjeneste = innsynTjeneste;
    }

    @PostMapping(value = "/send")
    public Kvittering send(@Valid @RequestBody Søknad søknad) {
        Person søker = oppslag.getSøker();
        MDC.put("Nav-Aktør-Id", søker.aktørId.getId());
        return sender.send(søknad, søker);
    }

    @PostMapping(value = "/ettersend")
    public Kvittering send(@Valid @RequestBody Ettersending ettersending) {
        return sender.send(ettersending, oppslag.getSøker());
    }

    @PostMapping(value = "/endre")
    public Kvittering send(@Valid @RequestBody EndringsSøknad endringsSøknad) {
        return sender.send(endringsSøknad, oppslag.getSøker());
    }

    @GetMapping(value = "/soknad")
    public Søknad søknad(@RequestParam(name = "behandlingId") String behandlingId) {
        return innsynTjeneste.hentSøknad(behandlingId);
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "jordboer") String navn) {
        LOG.info("Jeg ble pinget");
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @GetMapping(value = "/saker")
    public List<SakStatus> saker() {
        List<SakStatus> saker = innsynTjeneste.hentSaker(oppslag.getAktørId());
        if (EnvUtil.isDevOrPreprod(env)) {
            try {
                if (!saker.isEmpty()) {
                    String saksnummer = saker.get(0).getSaksnummer();
                    LOG.trace(EnvUtil.CONFIDENTIAL, "Tester ettersending mot sak {}", saksnummer);
                    ValgfrittVedlegg vedlegg = new ValgfrittVedlegg(DokumentType.I500002,
                            new ClassPathResource("sykkel.pdf"));
                    Ettersending es = new Ettersending(saksnummer, vedlegg);
                    sender.send(es, oppslag.getSøker());
                }
                else {
                    LOG.trace("Ingen saker å ettersende til");
                }
            } catch (IOException e) {
                LOG.error("Funkade inte", e);
            }
        }
        return saker;

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [sender=" + sender + ", innsynTjeneste=" + innsynTjeneste + ", oppslag=" + oppslag + "]";
    }

}
