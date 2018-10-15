package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArbeidsforholdMapperTest {

    private static Stream<Arguments> valueProvider() {
        Organisasjon orgUtenNavn = new Organisasjon();
        orgUtenNavn.setOrgnummer("12345");

        HistoriskArbeidsgiverMedArbeidsgivernummer histUtenNavn = new HistoriskArbeidsgiverMedArbeidsgivernummer();
        histUtenNavn.setArbeidsgivernummer("12346");

        Person person = new Person();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent("12347");
        person.setIdent(norskIdent);

        return Stream.of(
            Arguments.of("12345", "orgnr", orgUtenNavn),
            Arguments.of("12346", "arbeidsgivernr", histUtenNavn),
            Arguments.of("12347", "fnr", person)
        );
    }

    @ParameterizedTest
    @MethodSource("valueProvider")
    public void mapValues(String arbeidsgiverId, String arbeidsgiverIdType, Aktoer aktoer) {
        no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold = TestdataProvider.forhold(aktoer);

        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(2);
        Arbeidsforhold expected =
            new Arbeidsforhold(arbeidsgiverId, arbeidsgiverIdType, 100d, earlier, Optional.of(now));
        Arbeidsforhold actual = ArbeidsforholdMapper.map(forhold);

        assertEquals(expected, actual);
    }

}