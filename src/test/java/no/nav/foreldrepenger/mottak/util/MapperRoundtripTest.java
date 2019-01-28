package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.NORSK_FORELDER_FNR;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.endringssøknad;
import static no.nav.foreldrepenger.mottak.domain.foreldrepenger.ForeldrepengerTestUtils.søknadMedToVedlegg;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.DelegerendeDomainMapper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.V1DomainMapper;
import no.nav.foreldrepenger.mottak.innsending.foreldrepenger.V2DomainMapper;
import no.nav.foreldrepenger.mottak.innsyn.DelegerendeXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.innsyn.V1ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.V2ForeldrepengerXMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.XMLMapper;
import no.nav.foreldrepenger.mottak.innsyn.XMLStreamSøknadInspektør;
import no.nav.foreldrepenger.mottak.oppslag.Oppslag;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class MapperRoundtripTest {

    private static final AktorId SØKER = new AktorId("42");

    private static final AktorId ID = SØKER;

    @Mock
    private Oppslag oppslag;
    private DelegerendeDomainMapper domainMapper;
    private XMLMapper xmlMapper;

    @BeforeEach
    public void before() {
        when(oppslag.getFnr(eq(ID))).thenReturn(NORSK_FORELDER_FNR);
        when(oppslag.getAktørId(eq(NORSK_FORELDER_FNR))).thenReturn(ID);
        domainMapper = new DelegerendeDomainMapper(new V1DomainMapper(oppslag), new V2DomainMapper(oppslag));
        xmlMapper = new DelegerendeXMLMapper(new V1ForeldrepengerXMLMapper(oppslag),
                new V2ForeldrepengerXMLMapper(oppslag));
    }

    @Test
    public void testFørstegangssøknadRoundtrip() {
        Versjon.alleVersjoner()
                .stream()
                .forEach(this::roundTripInitiell);
    }

    // @Test
    public void testEndringRoundtrip() {
        Versjon.alleVersjoner()
                .stream()
                .forEach(this::roundTripEndring);
    }

    private void roundTripInitiell(Versjon v) {
        Søknad søknad = søknadMedToVedlegg(v);
        String xml = domainMapper.tilXML(søknad, SØKER, v);
        SøknadEgenskap egenskaper = new XMLStreamSøknadInspektør().inspiser(xml);
        assertEquals(søknad, xmlMapper.tilSøknad(xml, egenskaper));
    }

    private void roundTripEndring(Versjon v) {
        Endringssøknad søknad = endringssøknad(v);
        String xml = domainMapper.tilXML(søknad, SØKER, v);
        SøknadEgenskap egenskaper = new XMLStreamSøknadInspektør().inspiser(xml);
        assertEquals(søknad, xmlMapper.tilSøknad(xml, egenskaper));
    }
}
