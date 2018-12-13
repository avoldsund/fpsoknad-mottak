package no.nav.foreldrepenger.mottak.util;

import static no.nav.foreldrepenger.mottak.http.Constants.ISSUER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.mottak.domain.Fødselsnummer;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnauthenticatedException;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TokenHelperTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
    @Mock
    private OIDCRequestContextHolder holder;
    @Mock
    private OIDCValidationContext context;
    @Mock
    private OIDCClaims claims;

    private TokenHelper tokenHelper;

    @Before
    public void before() {
        when(holder.getOIDCValidationContext()).thenReturn(context);
        when(context.getClaims(eq(ISSUER))).thenReturn(claims);
        tokenHelper = new TokenHelper(holder);
    }

    @Test
    public void testExtractorOK() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().subject(FNR.getFnr()).build());
        assertEquals(FNR, tokenHelper.autentisertBruker());
        assertTrue(tokenHelper.erAutentisert());
    }

    @Test(expected = UnauthenticatedException.class)
    public void testExtractorNoContext() {
        when(holder.getOIDCValidationContext()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        tokenHelper.autentisertBruker();
    }

    @Test(expected = UnauthenticatedException.class)
    public void testExtractorNoClaims() {
        when(context.getClaims(eq("selvbetjening"))).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        tokenHelper.autentisertBruker();
    }

    @Test(expected = UnauthenticatedException.class)
    public void testExtractorNoClaimset() {
        when(claims.getClaimSet()).thenReturn(null);
        assertFalse(tokenHelper.erAutentisert());
        tokenHelper.autentisertBruker();
    }

    @Test(expected = UnauthenticatedException.class)
    public void testExtractorNoSubject() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().build());
        assertFalse(tokenHelper.erAutentisert());
        tokenHelper.autentisertBruker();
    }
}