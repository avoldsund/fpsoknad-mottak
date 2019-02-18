package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static no.nav.foreldrepenger.mottak.domain.Kvittering.IKKE_SENDT;
import static no.nav.foreldrepenger.mottak.innsending.SøknadSender.FPFORDEL_SENDER;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.ES_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FPFORDEL_SEND_INITIELL;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ENDRING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_ETTERSSENDING;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_FØRSTEGANG;
import static no.nav.foreldrepenger.mottak.util.CounterRegistry.FP_SENDFEIL;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.Kvittering;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.SøknadSender;
import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;

@Service
@Qualifier(FPFORDEL_SENDER)
public class FPFordelSøknadSender implements SøknadSender {

    private static final Logger LOG = LoggerFactory.getLogger(FPFordelSøknadSender.class);

    private final FPFordelConnection connection;
    private final FPFordelKonvoluttGenerator konvoluttGenerator;

    @Inject
    public FPFordelSøknadSender(FPFordelConnection connection, FPFordelKonvoluttGenerator konvoluttGenerator) {
        this.connection = connection;
        this.konvoluttGenerator = konvoluttGenerator;
    }

    public void ping() {
        LOG.info("Pinger");
        connection.ping();
    }

    @Override
    public Kvittering send(Endringssøknad endringsSøknad, Person søker, SøknadEgenskap egenskap) {
        return send(egenskap.getType(), konvoluttGenerator.payload(endringsSøknad, søker, egenskap));
    }

    @Override
    public Kvittering send(Søknad søknad, Person søker, SøknadEgenskap egenskap) {
        return send(egenskap.getType(), konvoluttGenerator.payload(søknad, søker, egenskap));
    }

    @Override
    public Kvittering send(Ettersending ettersending, Person søker, SøknadEgenskap egenskap) {
        return send(egenskap.getType(), konvoluttGenerator.payload(ettersending, søker));
    }

    private Kvittering send(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        if (connection.isEnabled()) {
            return doSend(type, payload);
        }
        LOG.info("Sending av {} til FPFordel er deaktivert, ingenting å sende", type);
        return IKKE_SENDT;
    }

    private Kvittering doSend(SøknadType type, HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload) {
        try {
            LOG.info("Sender {} til FPFordel", type.name().toLowerCase());
            Kvittering kvittering = connection.send(payload);
            LOG.info("Sendte {} til FPFordel", type.name().toLowerCase());
            logAndCount(type);
            LOG.info("Returnerer kvittering {}", kvittering);
            return kvittering;
        } catch (Exception e) {
            FP_SENDFEIL.increment();
            throw e;
        }
    }

    private static void logAndCount(SøknadType type) {
        switch (type) {
        case INITIELL_ENGANGSSTØNAD:
            ES_FØRSTEGANG.increment();
            break;
        case ENDRING_FORELDREPENGER:
            FP_ENDRING.increment();
            break;
        case ETTERSENDING_ENGANGSSTØNAD:
            ES_ETTERSSENDING.increment();
            break;
        case ETTERSENDING_FORELDREPENGER:
            FP_ETTERSSENDING.increment();
            break;
        case INITIELL_FORELDREPENGER:
            FPFORDEL_SEND_INITIELL.increment();
            FP_FØRSTEGANG.increment();
            break;
        default:
            break;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [connection=" + connection + ", konvoluttGenerator=" + konvoluttGenerator
                + "]";
    }

}
