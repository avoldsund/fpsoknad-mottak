package no.nav.foreldrepenger.lookup.ws.aktor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AktorIdClientWs implements AktorIdClient {
    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientWs.class);

    private final AktoerV2 aktoerV2;
    private final AktoerV2 healthIndicator;

    private static final Counter ERROR_COUNTER_AKTOR = Metrics.counter("errors.lookup.aktorid");
    private static final Counter ERROR_COUNTER_FNR = Metrics.counter("errors.lookup.fnr");
    private static final Timer timerFnr = Metrics.timer("lookup.fnr");
    private static final Timer timerAktorId = Metrics.timer("lookup.aktorid");

    public AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator) {
        this.aktoerV2 = Objects.requireNonNull(aktoerV2);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
    }

    @Override
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        long start = System.currentTimeMillis();
        try {
            return new AktorId(aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId());
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            LOG.warn("Henting av aktørid har feilet", e);
            throw new NotFoundException(e.getMessage());
        } catch (Exception ex) {
            ERROR_COUNTER_AKTOR.increment();
            throw ex;
        } finally {
            long end = System.currentTimeMillis();
            timerAktorId.record(end - start, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public Fødselsnummer fnrForAktørId(AktorId aktørId) {
        long start = System.currentTimeMillis();
        try {
            return new Fødselsnummer(aktoerV2.hentIdentForAktoerId(request(aktørId)).getIdent());
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.warn("Henting av fnr har feilet", e);
            throw new NotFoundException(e.getMessage());
        } catch (Exception ex) {
            ERROR_COUNTER_FNR.increment();
            throw ex;
        } finally {
            long end = System.currentTimeMillis();
            timerFnr.record(end - start, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger Aktørregisteret");
            healthIndicator.ping();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static HentIdentForAktoerIdRequest request(AktorId aktørId) {
        HentIdentForAktoerIdRequest req = new HentIdentForAktoerIdRequest();
        req.setAktoerId(aktørId.getAktør());
        return req;
    }

    private static HentAktoerIdForIdentRequest request(Fødselsnummer fnr) {
        HentAktoerIdForIdentRequest req = new HentAktoerIdForIdentRequest();
        req.setIdent(fnr.getFnr());
        return req;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + "]";
    }

}
