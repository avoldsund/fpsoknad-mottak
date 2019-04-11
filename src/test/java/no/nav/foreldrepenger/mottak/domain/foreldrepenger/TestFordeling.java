package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.ukeDagNær;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.MorsAktivitet.ARBEID_OG_UTDANNING;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Oppholdsårsak.UTTAK_FEDREKVOTE_ANNEN_FORELDER;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak.IKKE_RETT_ANNEN_FORELDER;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.INSTITUSJONSOPPHOLD_BARNET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OppholdsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.OverføringsPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UtsettelsesPeriode;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.fordeling.UttaksPeriode;

public class TestFordeling {

    @Test
    public void testFordelingUttakFørstErUtsettelse() {

        LocalDate utsettelseStart = LocalDate.now().plusMonths(2);
        Fordeling f = new Fordeling(true, IKKE_RETT_ANNEN_FORELDER, newArrayList(
                new OppholdsPeriode(ukeDagNær(LocalDate.now().plusMonths(1)), ukeDagNær(utsettelseStart),
                        UTTAK_FEDREKVOTE_ANNEN_FORELDER, null),
                new OverføringsPeriode(ukeDagNær(LocalDate.now()), ukeDagNær(LocalDate.now().plusMonths(1)),
                        Overføringsårsak.ALENEOMSORG, FEDREKVOTE, null),
                new UtsettelsesPeriode(ukeDagNær(utsettelseStart),
                        ukeDagNær(LocalDate.now().plusMonths(3)), true, Collections.singletonList("222"),
                        INSTITUSJONSOPPHOLD_BARNET, FEDREKVOTE, null,
                        null),
                new UttaksPeriode(ukeDagNær(LocalDate.now().plusMonths(3)), ukeDagNær(LocalDate.now().plusMonths(4)),
                        FEDREKVOTE,
                        true, ARBEID_OG_UTDANNING, true, 75.0d, null)));
        assertEquals(utsettelseStart, f.getFørsteUttaksdag());
    }

    @Test
    public void testFordelingUttakFørstErUttak() {

        LocalDate uttakStart = LocalDate.now().minusMonths(2);
        Fordeling f = new Fordeling(true, IKKE_RETT_ANNEN_FORELDER, newArrayList(
                new OppholdsPeriode(ukeDagNær(LocalDate.now().plusMonths(1)), ukeDagNær(LocalDate.now().plusMonths(2)),
                        UTTAK_FEDREKVOTE_ANNEN_FORELDER, null),
                new OverføringsPeriode(ukeDagNær(LocalDate.now()), ukeDagNær(LocalDate.now().plusMonths(1)),
                        Overføringsårsak.ALENEOMSORG, FEDREKVOTE, null),
                new UtsettelsesPeriode(ukeDagNær(LocalDate.now().plusMonths(2)),
                        ukeDagNær(LocalDate.now().plusMonths(3)), true, Collections.singletonList("222"),
                        INSTITUSJONSOPPHOLD_BARNET, FEDREKVOTE, null,
                        null),
                new UttaksPeriode(ukeDagNær(uttakStart), ukeDagNær(LocalDate.now().plusMonths(4)),
                        FEDREKVOTE,
                        true, ARBEID_OG_UTDANNING, true, 75.0d, null)));
        assertEquals(uttakStart, f.getFørsteUttaksdag());
    }

    @Test
    public void testIngenFørsteDag() {

        Fordeling f = new Fordeling(true, IKKE_RETT_ANNEN_FORELDER, newArrayList(

                new OverføringsPeriode(ukeDagNær(LocalDate.now()), ukeDagNær(LocalDate.now().plusMonths(1)),
                        Overføringsårsak.ALENEOMSORG, FEDREKVOTE, null)));
        assertNull(f.getFørsteUttaksdag());
    }

}
