package no.nav.foreldrepenger.oppslag.lookup.ws.arbeidsforhold;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.oppslag.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public class ArbeidsforholdClientWs implements ArbeidsforholdClient {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);

    private final ArbeidsforholdV3 arbeidsforholdV3;
    private final ArbeidsforholdV3 healthIndicator;
    private final OrganisasjonClient orgClient;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.aareg");

    public ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator, OrganisasjonClient orgClient) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.healthIndicator = healthIndicator;
        this.orgClient = orgClient;
    }

    public void ping() {
        try {
            LOG.info("Pinger AAreg");
            healthIndicator.ping();
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw ex;
        }
    }

    public List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr) {
        try {
            FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3.finnArbeidsforholdPrArbeidstaker(request(fnr));

            return response.getArbeidsforhold().stream()
                .map(ArbeidsforholdMapper::map)
                .map(this::addArbeidsgiverNavn)
                .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning ex) {
            LOG.warn("Sikkerhetsfeil fra AAREG", ex);
            throw new ForbiddenException(ex);
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput ex) {
            throw new IncompleteRequestException(ex);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }
    }

    private FinnArbeidsforholdPrArbeidstakerRequest request(Fodselsnummer fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();

        NorskIdent ident = new NorskIdent();
        ident.setIdent(fnr.getFnr());
        request.setIdent(ident);

        Regelverker regelverker = new Regelverker();
        regelverker.setValue("ALLE");
        request.setRapportertSomRegelverk(regelverker);

        return request;
    }

    private Arbeidsforhold addArbeidsgiverNavn(Arbeidsforhold arbeidsforhold) {
        arbeidsforhold.setArbeidsgiverNavn(orgClient.nameFor(arbeidsforhold.getArbeidsgiverId()).orElse("Ukjent navn"));
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return "AaregClient{" +
                "arbeidsforholdV3=" + arbeidsforholdV3 +
                '}';
    }
}