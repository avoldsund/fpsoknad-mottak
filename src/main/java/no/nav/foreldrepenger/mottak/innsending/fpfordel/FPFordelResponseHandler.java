package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.FP_FORDEL_MESSED_UP;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_FPSAK;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_GOSYS;
import static no.nav.foreldrepenger.mottak.domain.LeveranseStatus.SENDT_OG_MOTATT_FPSAK;
import static org.springframework.http.HttpHeaders.LOCATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import no.nav.foreldrepenger.mottak.domain.Kvittering;

@Component
public class FPFordelResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelResponseHandler.class);
    private final RestTemplate template;
    private final int maxAntallForsøk;

    public FPFordelResponseHandler(RestTemplate template,
            @Value("${fpfordel.max:3}") int maxAntallForsøk) {
        this.template = template;
        this.maxAntallForsøk = maxAntallForsøk;
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> kvittering, String ref) {
        return handle(kvittering, ref, maxAntallForsøk);
    }

    public Kvittering handle(ResponseEntity<FPFordelKvittering> respons, String ref, int n) {
        LOG.info("Fikk respons {}", respons);
        if (!respons.hasBody()) {
            LOG.warn("Fikk ingen kvittering");
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }
        switch (respons.getStatusCode()) {
        case ACCEPTED:
            return handlePolling(respons, ref, n);
        case SEE_OTHER:
            return sendtOgMotattKvittering(ref, FPSakFordeltKvittering.class.cast(respons.getBody()));
        case OK:
            return gosysKvittering(ref, FPFordelGosysKvittering.class.cast(respons.getBody()));
        default:
            LOG.warn("Fikk uventet response kode {}", respons.getStatusCode());
            return new Kvittering(FP_FORDEL_MESSED_UP);
        }
    }

    private Kvittering handlePolling(ResponseEntity<FPFordelKvittering> respons, String ref, int n) {

        String location = respons.getHeaders().getFirst(LOCATION);
        if (location != null) {
            LOG.info("Fikk location header {}", location);
            if (!respons.hasBody()) {
                LOG.info("Fikk ingen kvittering", location);
                return new Kvittering(FP_FORDEL_MESSED_UP);
            }
            return pollUntil(location, ref, pollDuration(respons), n);
        }
        LOG.info("Fikk ingen location header{}");
        return new Kvittering(FP_FORDEL_MESSED_UP);

    }

    private static long pollDuration(ResponseEntity<FPFordelKvittering> respons) {
        return FPFordelPendingKvittering.class.cast(respons.getBody()).getPollInterval().toMillis();
    }

    private Kvittering pollUntil(String pollURI, String ref, long mills, int n) {
        LOG.info("Søknaden er mottatt, men ikke behandlet i FPSak");
        if (n > 0) {
            return poll(pollURI, ref, mills, n - 1);
        }
        LOG.info("Pollet FPFordel {} ganger, uten å få svar, gir opp", maxAntallForsøk);
        return new Kvittering(ref, SENDT_FPSAK);
    }

    private static Kvittering gosysKvittering(String ref, FPFordelGosysKvittering gosysKvittering) {
        LOG.info("Søknaden er sendt til manuell behandling i Gosys");
        Kvittering kvittering = new Kvittering(ref, SENDT_GOSYS);
        kvittering.setJournalId(gosysKvittering.getJounalId());
        return kvittering;
    }

    private static Kvittering sendtOgMotattKvittering(String ref, FPSakFordeltKvittering fordeltKvittering) {
        LOG.info("Søknaden er motatt og behandlet av FPSak, journalId er {}, saksnummer er {}",
                fordeltKvittering.getJounalId(), fordeltKvittering.getSaksnummer());
        Kvittering kvittering = new Kvittering(ref, SENDT_OG_MOTATT_FPSAK);
        kvittering.setJournalId(fordeltKvittering.getJounalId());
        kvittering.setSaksNr(fordeltKvittering.getSaksnummer());
        return kvittering;
    }

    private Kvittering poll(String pollURI, String ref, long pollDuration, int n) {
        try {
            Thread.sleep(pollDuration);
            LOG.info("Poller  {} for {}. gang av {}", pollURI, maxAntallForsøk - n, maxAntallForsøk);
            return handle(template.getForEntity(pollURI, FPFordelKvittering.class), ref, n);
        } catch (RestClientException | InterruptedException e) {
            LOG.warn("Kunne ikke polle FPFordel på {}", pollURI, e);
            throw new FPFordelUnavailableException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [template=" + template + ", maxAntallForsøk=" + maxAntallForsøk + "]";
    }
}
