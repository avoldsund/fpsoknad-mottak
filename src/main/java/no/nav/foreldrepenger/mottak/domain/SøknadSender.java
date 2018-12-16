package no.nav.foreldrepenger.mottak.domain;

import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface SøknadSender {
    String DOKMOT = "dokmot";
    String FPFORDEL = "fpfordel";
    String DUAL = "dual";
    Versjon DEFAULT_VERSJON = Versjon.V1;

    Kvittering send(Søknad søknad, Person søker, Versjon versjon);

    Kvittering send(Ettersending ettersending, Person søker, Versjon versjon);

    Kvittering send(Endringssøknad endringsøknad, Person søker, Versjon versjon);

    default Kvittering send(Søknad søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

    default Kvittering send(Ettersending søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

    default Kvittering send(Endringssøknad søknad, Person søker) {
        return send(søknad, søker, DEFAULT_VERSJON);
    }

}
