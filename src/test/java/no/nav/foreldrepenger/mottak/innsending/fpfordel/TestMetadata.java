package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedEttOpplastetEttIkkeOpplastetVedlegg;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.FPFordelMetadata;

public class TestMetadata {

    @Test
    public void testIgnorerIkkeLastetOpp() {
        Søknad søknad = søknadMedEttOpplastetEttIkkeOpplastetVedlegg();
        FPFordelMetadata metadata = new FPFordelMetadata(søknad, new AktorId("123"), "42");
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(3, metadata.getFiler().size());
        assertEquals("123", metadata.getBrukerId());
    }

    @Test
    public void test2LastetOpp() {
        Søknad søknad = søknadMedToVedlegg();
        FPFordelMetadata metadata = new FPFordelMetadata(søknad, new AktorId("123"), "42");
        assertEquals(2, søknad.getVedlegg().size());
        assertEquals(4, metadata.getFiler().size());
    }
}
