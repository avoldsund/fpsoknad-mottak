package no.nav.foreldrepenger.mottak.innsyn;

import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Foreldrepenger;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.SøknadType;
import no.nav.foreldrepenger.mottak.util.Versjon;

public interface SøknadInspektør {

    SøknadEgenskap inspiser(String xml);

    default SøknadEgenskap inspiser(Søknad søknad) {
        Ytelse ytelse = søknad.getYtelse();
        return ytelse instanceof Foreldrepenger ? SøknadEgenskap.INITIELL_FORELDREPENGER
                : new SøknadEgenskap(SøknadType.INITIELL_ENGANGSSTØNAD);
    }

    default SøknadType type(Søknad søknad) {
        return inspiser(søknad).getType();
    }

    default SøknadType type(String xml) {
        return inspiser(xml).getType();
    }

    default Versjon versjon(String xml) {
        return inspiser(xml).getVersjon();
    }

}
