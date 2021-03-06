package no.nav.foreldrepenger.mottak.oppslag;

import org.springframework.stereotype.Service;

import no.nav.foreldrepenger.boot.conditionals.ConditionalOnLocal;
import no.nav.security.token.support.core.jwt.JwtToken;

@Service
@ConditionalOnLocal
public class LocalSystemUserTokenService implements SystemUserTokenService {

    @Override
    public SystemToken getUserToken() {
        return new SystemToken(new JwtToken("hey"), 3600L, "jalla");
    }

}
