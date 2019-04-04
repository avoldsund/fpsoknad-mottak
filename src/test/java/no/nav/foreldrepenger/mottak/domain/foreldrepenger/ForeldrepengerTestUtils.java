package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Arrays.asList;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000062;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I000063;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500002;
import static no.nav.foreldrepenger.mottak.domain.felles.DokumentType.I500005;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.medlemsskap;
import static no.nav.foreldrepenger.mottak.domain.felles.TestUtils.søker;
import static no.nav.foreldrepenger.mottak.domain.felles.opptjening.Virksomhetstype.FISKE;
import static no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.Ytelse;
import no.nav.foreldrepenger.mottak.domain.felles.DokumentType;
import no.nav.foreldrepenger.mottak.domain.felles.Ettersending;
import no.nav.foreldrepenger.mottak.domain.felles.EttersendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.InnsendingsType;
import no.nav.foreldrepenger.mottak.domain.felles.ProsentAndel;
import no.nav.foreldrepenger.mottak.domain.felles.TestUtils;
import no.nav.foreldrepenger.mottak.domain.felles.ValgfrittVedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.felles.ÅpenPeriode;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjening;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.AnnenOpptjeningType;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.EgenNæring;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Frilans;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.FrilansOppdrag;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.NorskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.Regnskapsfører;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskArbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.felles.opptjening.UtenlandskOrganisasjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.GradertUttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UttaksPeriode;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.Svangerskapspenger;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.DelvisTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.HelTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.IngenTilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Frilanser;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.PrivatArbeidsgiver;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.SelvstendigNæringsdrivende;
import no.nav.foreldrepenger.mottak.domain.svangerskapspenger.tilrettelegging.arbeidsforhold.Virksomhet;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class ForeldrepengerTestUtils {

    public static final Fødselsnummer NORSK_FORELDER_FNR = new Fødselsnummer("01010111111");
    public static final String ID142 = "V142";
    public static final String ID143 = "V143";
    public static final String ID144 = "V144";

    public static final List<Vedlegg> TO_VEDLEGG = newArrayList(
            TestUtils.valgfrittVedlegg(ID142, InnsendingsType.LASTET_OPP),
            TestUtils.valgfrittVedlegg(ID143, InnsendingsType.LASTET_OPP));
    public static final ValgfrittVedlegg VEDLEGG1 = opplastetVedlegg(ID142, I500002);
    public static final ValgfrittVedlegg V2 = opplastetVedlegg(ID143, I500005);
    public static final ValgfrittVedlegg V3 = opplastetVedlegg(ID144, I000062);

    private static final ValgfrittVedlegg IKKE_OPPLASTETV1 = ikkeOpplastet(ID142, I000063);
    private static final ValgfrittVedlegg IKKE_OPPLASTETV2 = ikkeOpplastet(ID143, I000063);

    public static Søknad foreldrepengeSøknad(Versjon v) {
        return new Søknad(LocalDate.now(), TestUtils.søker(), foreldrepenger(v, false), VEDLEGG1);
    }

    public static Søknad foreldrepengeSøknadUtenVedlegg(Versjon v) {
        return new Søknad(LocalDate.now(), TestUtils.søker(), foreldrepenger(v, false));
    }

    public static Søknad svp() {
        return søknad(Versjon.V1, svangerskapspenger(vedleggRefs(VEDLEGG1)), VEDLEGG1);
    }

    public static Søknad søknadMedEttVedlegg(Versjon v) {
        return søknad(v, false, VEDLEGG1);
    }

    public static Søknad søknadMedEttOpplastetEttIkkeOpplastetVedlegg(Versjon v) {
        return søknad(v, false, VEDLEGG1, IKKE_OPPLASTETV2);
    }

    public static Søknad søknadMedToVedlegg(Versjon v) {
        return søknad(v, false, VEDLEGG1, V2);
    }

    public static Søknad søknadMedEttIkkeOpplastedVedlegg(Versjon v, boolean utland) {
        return søknad(v, utland, IKKE_OPPLASTETV1);
    }

    public static Søknad søknad(Versjon v, boolean utland, Vedlegg... vedlegg) {
        return søknad(v, foreldrepenger(v, utland, vedleggRefs(vedlegg)), vedlegg);
    }

    public static Søknad søknad(Versjon v, Ytelse ytelse, Vedlegg... vedlegg) {
        return new Søknad(LocalDate.now(), TestUtils.søker(), ytelse, asList(vedlegg));
    }

    public static Endringssøknad endringssøknad(Versjon v, Vedlegg... vedlegg) {
        return new Endringssøknad(LocalDate.now(), søker(),
                fordeling(v, vedleggRefs(vedlegg)), norskForelder(v),
                fødsel(v),
                rettigheter(v),
                "42" + v.name().toLowerCase(), vedlegg);
    }

    private static String[] vedleggRefs(Vedlegg... vedlegg) {
        return Arrays.stream(vedlegg)
                .map(s -> s.getId())
                .toArray(String[]::new);
    }

    public static Ettersending ettersending() {
        return new Ettersending(EttersendingsType.foreldrepenger, "42", TO_VEDLEGG);
    }

    public static Svangerskapspenger svangerskapspenger(String... vedleggRefs) {
        return Svangerskapspenger.builder()
                .termindato(LocalDate.now().plusMonths(1))
                .medlemsskap(medlemsskap(Versjon.V3))
                .opptjening(opptjening(Versjon.V3))
                .tilrettelegging(tilrettelegging(vedleggRefs))
                .build();
    }

    private static List<Tilrettelegging> tilrettelegging(String... vedleggRefs) {
        return Lists.newArrayList(helTilrettelegging(vedleggRefs), delvisTilrettelegging(vedleggRefs),
                ingenTilrettelegging(vedleggRefs));
    }

    private static Tilrettelegging ingenTilrettelegging(String... vedleggRefs) {
        return new IngenTilrettelegging(frilanser(), LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(2),
                asList(vedleggRefs));

    }

    private static Tilrettelegging delvisTilrettelegging(String... vedleggRefs) {
        return new DelvisTilrettelegging(privat(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2),
                ProsentAndel.of(100), asList(vedleggRefs));
    }

    private static Tilrettelegging helTilrettelegging(String... vedleggRefs) {
        return new HelTilrettelegging(virksomhet(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2),
                asList(vedleggRefs));

    }

    private static Arbeidsforhold virksomhet() {
        return new Virksomhet("888888888");
    }

    private static Arbeidsforhold privat() {
        return new PrivatArbeidsgiver("01010111111");
    }

    private static Arbeidsforhold frilanser() {
        return new Frilanser("risiko", "tiltak");
    }

    private static Arbeidsforhold selvstendig() {
        return new SelvstendigNæringsdrivende("risiko", "tiltak");
    }

    static Foreldrepenger foreldrepenger(Versjon v, boolean utland, String... vedleggRefs) {
        return Foreldrepenger.builder()
                .rettigheter(rettigheter(v))
                .annenForelder(norskForelder(v))
                .dekningsgrad(Dekningsgrad.GRAD100)
                .fordeling(fordeling(v, vedleggRefs))
                .opptjening(opptjening(v, vedleggRefs))
                .relasjonTilBarn(termin(v))
                .medlemsskap(medlemsskap(v, utland))
                .build();
    }

    static Opptjening opptjening(Versjon v, String... vedleggRefs) {
        return new Opptjening(Collections.singletonList(utenlandskArbeidsforhold(v, vedleggRefs)),
                egneNæringer(v, vedleggRefs),
                andreOpptjeninger(v, vedleggRefs), frilans(v, vedleggRefs));
    }

    private static Frilans frilans(Versjon v, String... vedleggRefs) {
        return new Frilans(åpenPeriode(v, true), true, true,
                newArrayList(
                        new FrilansOppdrag("fattern", åpenPeriode(v, true)),
                        new FrilansOppdrag(
                                "den andre bror min og samtidig en fryktelig lang tekst som straks må bryte over til ny linje",
                                åpenPeriode(v, true)),
                        new FrilansOppdrag("\u202Darbeidsgiver\u00A0med " + "\u0141" + "tegn\tsom normali\uFFFDseres",
                                åpenPeriode(v, true)),
                        new FrilansOppdrag("den fjerde bror min med \u2e84\u2e87 tegn som ikke kan encodes",
                                åpenPeriode(v, true)),
                        new FrilansOppdrag("far min", åpenPeriode(v, true))),
                Arrays.asList(vedleggRefs));

    }

    private static List<AnnenOpptjening> andreOpptjeninger(Versjon v, String... vedleggRefs) {
        return newArrayList(annenOpptjening(v, vedleggRefs));
    }

    private static List<EgenNæring> egneNæringer(Versjon v, String... vedleggRefs) {
        return newArrayList(utenlandskEgenNæring(v, vedleggRefs), norskEgenNæring(v, vedleggRefs));
    }

    static UtenlandskForelder utenlandskForelder(Versjon v) {
        return new UtenlandskForelder("42", CountryCode.SE, "Pedro Bandolero");
    }

    static NorskForelder norskForelder(Versjon v) {
        return new NorskForelder(NORSK_FORELDER_FNR, "Åge Mañana Pålsen");
    }

    static Adopsjon adopsjon(Versjon v) {
        return new Adopsjon(0, LocalDate.now(), true, null, LocalDate.now(),
                Collections.singletonList(LocalDate.now()));
    }

    static ÅpenPeriode åpenPeriode(Versjon v) {
        return åpenPeriode(v, false);
    }

    static ÅpenPeriode åpenPeriode(Versjon v, boolean end) {

        return end ? new ÅpenPeriode(LocalDate.now().minusMonths(5), LocalDate.now())
                : new ÅpenPeriode(LocalDate.now().minusMonths(5));
    }

    static Omsorgsovertakelse omsorgsovertakelse(Versjon v) {
        return new Omsorgsovertakelse(LocalDate.now(), SKAL_OVERTA_ALENE, LocalDate.now());
    }

    static UtenlandskOrganisasjon utenlandskEgenNæring(Versjon v, String... vedleggRefs) {
        return UtenlandskOrganisasjon.builder()
                .vedlegg(Arrays.asList(vedleggRefs))
                .registrertILand(CountryCode.UG)
                .periode(åpenPeriode(v))
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein Åge Skapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .erNyIArbeidslivet(false)
                .næringsinntektBrutto(100_000)
                .orgName("Utenlandsk org")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .beskrivelseEndring(
                        "Endringer skjer fort i verdens største land (utlandet) og ikke minst skjer det mye med linjebryting")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static NorskOrganisasjon norskEgenNæring(Versjon v, String... vedleggRefs) {
        return NorskOrganisasjon.builder()
                .vedlegg(Arrays.asList(vedleggRefs))
                .periode(åpenPeriode(v))
                .regnskapsførere(Collections.singletonList(new Regnskapsfører("Rein Åge Kapsfører", "+4746929061")))
                .erNyOpprettet(true)
                .erVarigEndring(true)
                .erNyIArbeidslivet(true)
                .erNyOpprettet(true)
                .næringsinntektBrutto(100_000)
                .orgName("Norsk org")
                .orgNummer("923609016")
                .virksomhetsTyper(Collections.singletonList(FISKE))
                .beskrivelseEndring("Ting endrer seg i Norge også")
                .nærRelasjon(true)
                .endringsDato(LocalDate.now()).build();
    }

    static AnnenOpptjening annenOpptjening(Versjon v, String... vedleggRefs) {
        switch (v) {
        case V1:
            return new AnnenOpptjening(AnnenOpptjeningType.VENTELØNN, åpenPeriode(v),
                    Arrays.asList(vedleggRefs));
        case V2:
        case V3:
            return new AnnenOpptjening(AnnenOpptjeningType.VENTELØNN_VARTPENGER, åpenPeriode(v),
                    Arrays.asList(vedleggRefs));
        default:
            throw new IllegalArgumentException();
        }
    }

    static UtenlandskArbeidsforhold utenlandskArbeidsforhold(Versjon v, String... vedleggRefs) {
        return UtenlandskArbeidsforhold.builder()
                .vedlegg(Arrays.asList(vedleggRefs))
                .arbeidsgiverNavn("Brzeziński")
                .land(CountryCode.PL)
                .periode(åpenPeriode(v)).build();
    }

    private static List<LukketPeriodeMedVedlegg> perioder(Versjon v, String... vedleggRefs) {
        return newArrayList(
                oppholdsPeriode(v, vedleggRefs),
                overføringsPeriode(v, vedleggRefs),
                utsettelsesPeriode(v, vedleggRefs),
                uttaksPeriode(v, vedleggRefs),
                gradertPeriode(v, vedleggRefs));
    }

    static FremtidigFødsel termin(Versjon v) {
        return new FremtidigFødsel(LocalDate.now(), LocalDate.now());
    }

    static Fødsel fødsel(Versjon v) {
        return new Fødsel(LocalDate.now().minusMonths(2));
    }

    static UttaksPeriode uttaksPeriode(Versjon v, String... vedleggRefs) {
        return new UttaksPeriode(ukeDagNær(LocalDate.now().plusMonths(3)), ukeDagNær(LocalDate.now().plusMonths(4)),
                FEDREKVOTE,
                true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 75.0d, Arrays.asList(vedleggRefs));
    }

    static UttaksPeriode gradertPeriode(Versjon v, String... vedleggRefs) {
        switch (v) {
        case V1:
            return new GradertUttaksPeriode(ukeDagNær(LocalDate.now().plusMonths(4)), LocalDate.now().plusMonths(5),
                    FEDREKVOTE,
                    true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 42d, 75d, true, true,
                    Collections.singletonList("22222222222"), null, null,
                    Arrays.asList(vedleggRefs));
        case V2:
            return new GradertUttaksPeriode(ukeDagNær(LocalDate.now().plusMonths(4)), LocalDate.now().plusMonths(5),
                    FEDREKVOTE,
                    true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 42d, 75d, true, true,
                    Collections.singletonList("22222222222"), null, null,
                    Arrays.asList(vedleggRefs));
        case V3:
            return new GradertUttaksPeriode(ukeDagNær(LocalDate.now().plusMonths(4)), LocalDate.now().plusMonths(5),
                    FEDREKVOTE,
                    true, MorsAktivitet.ARBEID_OG_UTDANNING, true, 42d, 75d, true, true,
                    Collections.singletonList("22222222222"), true, true,
                    Arrays.asList(vedleggRefs));
        default:
            throw new IllegalStateException(v.toString());
        }

    }

    static OverføringsPeriode overføringsPeriode(Versjon v, String... vedleggRefs) {
        return new OverføringsPeriode(ukeDagNær(LocalDate.now()), ukeDagNær(LocalDate.now().plusMonths(1)),
                Overføringsårsak.ALENEOMSORG, StønadskontoType.FEDREKVOTE, Arrays.asList(vedleggRefs));
    }

    static OppholdsPeriode oppholdsPeriode(Versjon v, String... vedleggRefs) {
        return new OppholdsPeriode(ukeDagNær(LocalDate.now().plusMonths(1)), ukeDagNær(LocalDate.now().plusMonths(2)),
                Oppholdsårsak.UTTAK_FEDREKVOTE_ANNEN_FORELDER,
                Arrays.asList(vedleggRefs));
    }

    static UtsettelsesPeriode utsettelsesPeriode(Versjon v, String... vedleggRefs) {
        return new UtsettelsesPeriode(ukeDagNær(LocalDate.now().plusMonths(2)),
                ukeDagNær(LocalDate.now().plusMonths(3)), true, Collections.singletonList("222"),
                UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARNET, StønadskontoType.FEDREKVOTE, null,
                Arrays.asList(vedleggRefs));

    }

    static Fordeling fordeling(Versjon v, String... vedleggRefs) {
        return new Fordeling(true, Overføringsårsak.IKKE_RETT_ANNEN_FORELDER, perioder(v, vedleggRefs));
    }

    static Rettigheter rettigheter(Versjon v) {
        return new Rettigheter(true, true, true, LocalDate.now());
    }

    private static ValgfrittVedlegg opplastetVedlegg(String id, DokumentType type) {
        try {
            return new ValgfrittVedlegg(id, InnsendingsType.LASTET_OPP, type,
                    new ClassPathResource("terminbekreftelse.pdf"));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static ValgfrittVedlegg ikkeOpplastet(String id, DokumentType type) {
        try {
            return new ValgfrittVedlegg(id, InnsendingsType.SEND_SENERE, type, null);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private static LocalDate ukeDagNær(LocalDate dato) {
        LocalDate d = dato;
        while (!erUkedag(d)) {
            d = d.minusDays(1);
        }
        return d;
    }

    private static boolean erUkedag(LocalDate dato) {
        return !dato.getDayOfWeek().equals(SATURDAY) && !dato.getDayOfWeek().equals(SUNDAY);

    }
}
