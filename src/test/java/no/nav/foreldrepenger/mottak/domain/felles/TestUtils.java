package no.nav.foreldrepenger.mottak.domain.felles;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.mottak.domain.AktørId;
import no.nav.foreldrepenger.mottak.domain.BrukerRolle;
import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.domain.Navn;
import no.nav.foreldrepenger.mottak.domain.Søker;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.engangsstønad.Engangsstønad;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.AnnenForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.mottak.domain.felles.annenforelder.UtenlandskForelder;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.felles.medlemskap.Utenlandsopphold;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Adopsjon;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.FremtidigFødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Fødsel;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.Omsorgsovertakelse;
import no.nav.foreldrepenger.mottak.domain.felles.relasjontilbarn.RelasjonTilBarn;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class TestUtils {
    public static boolean hasPdfSignature(byte[] bytes) {
        return bytes[0] == 0x25 &&
                bytes[1] == 0x50 &&
                bytes[2] == 0x44 &&
                bytes[3] == 0x46;
    }

    public static List<Versjon> alleSøknadVersjoner() {
        return Lists.newArrayList(DEFAULT_VERSJON);
    }

    public static Søknad engangssøknad(Versjon v, boolean utland) {
        return engangssøknad(v, utland, termin(), norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, RelasjonTilBarn relasjon, boolean utland) {
        return engangssøknad(v, utland, relasjon, norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, RelasjonTilBarn relasjon) {
        return engangssøknad(v, false, relasjon, norskForelder(v));
    }

    public static Søknad engangssøknad(Versjon v, Vedlegg... vedlegg) {
        return engangssøknad(v, true, termin(), norskForelder(v), vedlegg);
    }

    public static Søknad engangssøknad(Versjon v, boolean utland, RelasjonTilBarn relasjon, AnnenForelder annenForelder,
            Vedlegg... vedlegg) {
        Søknad s = new Søknad(LocalDate.now(), søker(), engangstønad(v, utland, relasjon, annenForelder), vedlegg);
        s.setBegrunnelseForSenSøknad("Glemte hele ungen");
        s.setTilleggsopplysninger("Intet å tilføye");
        return s;
    }

    public static Engangsstønad engangstønad(Versjon v, boolean utland, RelasjonTilBarn relasjon,
            AnnenForelder annenForelder) {
        Engangsstønad stønad = new Engangsstønad(medlemsskap(v, utland), relasjon);
        stønad.setAnnenForelder(annenForelder);
        return stønad;
    }

    public static Utenlandsopphold utenlandsopphold() {
        return new Utenlandsopphold(CountryCode.SE, varighet());
    }

    public static NorskForelder norskForelder(Versjon v) {
        return new NorskForelder(fnr(), "Far Farsen");
    }

    public static UtenlandskForelder utenlandskForelder() {
        return new UtenlandskForelder("123456", CountryCode.SE, "Far Farsen");
    }

    public static Medlemsskap medlemsskap(Versjon v) {
        return medlemsskap(v, false);
    }

    public static Medlemsskap medlemsskap(Versjon v, boolean utland) {
        if (utland) {
            return new Medlemsskap(tidligereOppHoldIUtlandet(), framtidigOppHoldIUtlandet());
        }
        return new Medlemsskap(tidligereOppHoldINorge(), framtidigOppholdINorge());
    }

    static TidligereOppholdsInformasjon tidligereOppHoldIUtlandet() {
        List<Utenlandsopphold> utenlandOpphold = new ArrayList<>();
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.AT,
                new LukketPeriode(LocalDate.now().minusYears(1), LocalDate.now().minusMonths(6).minusDays(1))));
        utenlandOpphold.add(new Utenlandsopphold(CountryCode.FI,
                new LukketPeriode(LocalDate.now().minusMonths(6), LocalDate.now())));
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.ARBEIDET_I_UTLANDET, utenlandOpphold);
    }

    static TidligereOppholdsInformasjon tidligereOppHoldINorge() {
        return new TidligereOppholdsInformasjon(ArbeidsInformasjon.ARBEIDET_I_NORGE, emptyList());
    }

    public static Omsorgsovertakelse omsorgsovertakelse() {
        Omsorgsovertakelse overtakelse = new Omsorgsovertakelse(nå(), OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE,
                forrigeMåned());
        overtakelse.setBeskrivelse("dette er en beskrivelse");
        return overtakelse;
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id) {
        return påkrevdVedlegg(id, "pdf/terminbekreftelse.pdf");
    }

    public static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type) {
        return valgfrittVedlegg(id, type, "pdf/terminbekreftelse.pdf");
    }

    public static PåkrevdVedlegg påkrevdVedlegg(String id, String name) {
        return new PåkrevdVedlegg(id, DokumentType.I000062, new ClassPathResource(name));
    }

    static ValgfrittVedlegg valgfrittVedlegg(String id, InnsendingsType type, String name) {
        return new ValgfrittVedlegg(id, type, DokumentType.I000062,
                new ClassPathResource(name));
    }

    public static Adopsjon adopsjon() {
        return new Adopsjon(1, nå(), false, emptyList(), nå(), listeMedNå());
    }

    public static RelasjonTilBarn fødsel() {
        return fødsel(forrigeMåned());
    }

    public static RelasjonTilBarn fødsel(LocalDate date) {
        return new Fødsel(date);
    }

    public static FramtidigOppholdsInformasjon framtidigOppHoldIUtlandet() {
        List<Utenlandsopphold> opphold = new ArrayList<>();
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.GR,
                new LukketPeriode(LocalDate.now(), LocalDate.now().plusMonths(6))));
        opphold.add(new Utenlandsopphold(CountryCode.DE,
                new LukketPeriode(LocalDate.now().plusMonths(6).plusDays(1), LocalDate.now().plusYears(1))));
        return new FramtidigOppholdsInformasjon(opphold);
    }

    public static FramtidigOppholdsInformasjon framtidigOppholdINorge() {
        return new FramtidigOppholdsInformasjon(emptyList());
    }

    public static String serialize(Object obj, boolean print, ObjectMapper mapper) {
        try {
            String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return print ? printSerialized(serialized) : serialized;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String printSerialized(String serialized) {
        return serialized;
    }

    public static Søker søker() {
        return søker(navn());
    }

    public static Søker søker(Navn navn) {
        return new Søker(BrukerRolle.MOR);
    }

    private static Navn navn() {
        return new Navn("Mor", "Godhjerta", "Morsen", Kjønn.K);
    }

    public static RelasjonTilBarn termin() {
        return new FremtidigFødsel(nesteMåned(), forrigeMåned());
    }

    public static LukketPeriode varighet() {
        return new LukketPeriode(nå(), nesteMåned());
    }

    public static LocalDate nesteMåned() {
        return nå().plus(enMåned());
    }

    static LocalDate forrigeMåned() {
        return nå().minusMonths(1);
    }

    static Period enMåned() {
        return måned(1);
    }

    static Period måned(int n) {
        return Period.ofMonths(n);
    }

    public static LocalDate nå() {
        return LocalDate.now();
    }

    public static List<LocalDate> listeMedNå() {
        return singletonList(nå());
    }

    static LocalDate ettÅrSiden() {
        return LocalDate.now().minus(Period.ofYears(1));
    }

    public static AktørId aktoer() {
        return new AktørId("11111111111111111");
    }

    static Fødselsnummer fnr() {
        return new Fødselsnummer("01010111111");
    }

    public static Navn navnUtenMellomnavn() {
        return new Navn("Mor", null, "Monsen", Kjønn.K);
    }

    public static AnnenForelder ukjentForelder() {
        return new UkjentForelder();
    }

    public static Person person() {
        Person person = new Person(new Fødselsnummer("010101010101"), "Mor", "Mellommor", "Morsen", Kjønn.K,
                LocalDate.now().minusYears(25), "NN",
                CountryCode.NO, false,
                new Bankkonto("2000.20.20000", "Store Fiskerbank"));
        person.setAktørId(new AktørId("42"));
        return person;
    }

    public static String load(String file) throws IOException {
        return copyToString(new ClassPathResource(file).getInputStream(), defaultCharset());
    }
}
