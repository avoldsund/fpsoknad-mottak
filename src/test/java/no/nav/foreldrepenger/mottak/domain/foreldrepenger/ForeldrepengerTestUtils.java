package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.google.inject.internal.util.Lists.newArrayList;
import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.domain.felles.OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.Virksomhetstype.FISKE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.google.inject.internal.util.Lists;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;

public class ForeldrepengerTestUtils {

    public static final List<Vedlegg> ETT_VEDLEGG = Collections.singletonList(TestUtils.valgfrittVedlegg());
    private static final List<String> ETT_VEDLEGG_REF = Collections.singletonList("42");

    private static final List<LukketPeriodeMedVedlegg> PERIODER = perioder();

    public static Søknad foreldrepenger() {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(), ETT_VEDLEGG);
    }

    public static Søknad søknad(Vedlegg... vedlegg) {
        return new Søknad(LocalDateTime.now(), TestUtils.søker(), foreldrePenger(), asList(vedlegg));
    }

    static Foreldrepenger foreldrePenger() {
        return Foreldrepenger.builder()
                .rettigheter(rettigheter())
                .annenForelder(norskForelder())
                .dekningsgrad(Dekningsgrad.GRAD100)
                .fordeling(fordeling())
                .opptjening(opptjening())
                .relasjonTilBarn(termin())
                .medlemsskap(medlemsskap(false))
                .build();
    }

    static Opptjening opptjening() {
        return new Opptjening(Collections.singletonList(utenlandskArbeidsforhold()), egneNæringer(),
                andreOpptjeninger(), frilans());
    }

    private static Frilans frilans() {
        return new Frilans(åpenPeriode(true), false, false,
                Lists.newArrayList(new FrilansOppdrag("bror min", åpenPeriode(true)),
                        new FrilansOppdrag("far min", åpenPeriode(true))),
                Lists.newArrayList("42", "43"));

    }

    private static List<AnnenOpptjening> andreOpptjeninger() {
        return newArrayList(annenOpptjening());
    }

    private static List<EgenNæring> egneNæringer() {
        return newArrayList(utenlandskEgenNæring(), norskEgenNæring());
    }

    static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("42", CountryCode.SE, "Pedro Bandolero");
    }

    static NorskForelder norskForelder() {
        return new NorskForelder(new Fødselsnummer("01010111111"));
    }

    static Adopsjon adopsjon() {
        return new Adopsjon(LocalDate.now(), true, LocalDate.now());
    }

    static ÅpenPeriode åpenPeriode() {
        return åpenPeriode(false);
    }

    static ÅpenPeriode åpenPeriode(boolean end) {

        return end ? new ÅpenPeriode(LocalDate.now().minusMonths(5), LocalDate.now())
                : new ÅpenPeriode(LocalDate.now().minusMonths(5));
    }

    static Omsorgsovertakelse omsorgsovertakelse() {
        return new Omsorgsovertakelse(LocalDate.now(), SKAL_OVERTA_ALENE, LocalDate.now());
    }

    static UtenlandskOrganisasjon utenlandskEgenNæring() {
        return UtenlandskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein S. Kapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .næringsinntektBrutto(100_000)
                .orgName("My org")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .arbeidsland(CountryCode.SE).beskrivelseEndring("Stor endring")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static NorskOrganisasjon norskEgenNæring() {
        return NorskOrganisasjon.builder()
                .periode(åpenPeriode())
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein S. Kapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .næringsinntektBrutto(100_000)
                .orgName("My org")
                .orgNummer("82828282")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .arbeidsland(CountryCode.SE).beskrivelseEndring("Stor endring")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static AnnenOpptjening annenOpptjening() {
        return new AnnenOpptjening(AnnenOpptjeningType.LØNN_UNDER_UTDANNING, åpenPeriode(), null);
    }

    static UtenlandskArbeidsforhold utenlandskArbeidsforhold() {
        return UtenlandskArbeidsforhold.builder()
                .vedlegg(ETT_VEDLEGG_REF)
                .arbeidsgiverNavn("boss")
                .land(CountryCode.PL)
                .periode(åpenPeriode()).build();
    }

    private static List<LukketPeriodeMedVedlegg> perioder() {
        return newArrayList(oppholdsPeriode(), overføringsPeriode(), utsettelsesPeriode(), gradertPeriode());
    }

    static UttaksPeriode uttaksPeriode() {
        return new UttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG_REF,
                FEDREKVOTE, true, MorsAktivitet.ARBEID_OG_UTDANNING);
    }

    static UttaksPeriode gradertPeriode() {
        GradertUttaksPeriode periode = new GradertUttaksPeriode(LocalDate.now().minusMonths(1), LocalDate.now(),
                ETT_VEDLEGG_REF,
                FEDREKVOTE, true, MorsAktivitet.ARBEID_OG_UTDANNING);
        periode.setArbeidsForholdSomskalGraderes(true);
        periode.setArbeidstidProsent(75d);
        periode.setErArbeidstaker(true);
        periode.setVirksomhetsNummer("222222");
        return periode;
    }

    static FremtidigFødsel termin() {
        return new FremtidigFødsel(LocalDate.now(), LocalDate.now());
    }

    static OverføringsPeriode overføringsPeriode() {
        return new OverføringsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG_REF,
                Overføringsårsak.ALENEOMSORG);
    }

    static OppholdsPeriode oppholdsPeriode() {
        return new OppholdsPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG_REF,
                Oppholdsårsak.UTTAK_FELLSP_ANNEN_FORLDER);
    }

    static UtsettelsesPeriode utsettelsesPeriode() {
        return new UtsettelsesPeriode(LocalDate.now().minusMonths(1), LocalDate.now(), ETT_VEDLEGG_REF,
                UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARNET);
    }

    static Fordeling fordeling() {
        return new Fordeling(true, Overføringsårsak.IKKE_RETT_ANNEN_FORELDER, PERIODER);
    }

    static Rettigheter rettigheter() {
        return new Rettigheter(true, true, true);
    }
}
