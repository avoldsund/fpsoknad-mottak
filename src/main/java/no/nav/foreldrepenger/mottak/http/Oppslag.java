package no.nav.foreldrepenger.mottak.http;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.felles.Person;
import no.nav.foreldrepenger.mottak.pdf.Arbeidsforhold;

import java.util.List;

public interface Oppslag {

    Person getSøker();

    AktorId getAktørId();

    AktorId getAktørId(Fødselsnummer fnr);

    Fødselsnummer getFnr(AktorId aktørId);

    List<Arbeidsforhold> getArbeidsforhold();

}