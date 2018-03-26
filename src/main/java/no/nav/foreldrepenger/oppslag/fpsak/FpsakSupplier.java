package no.nav.foreldrepenger.oppslag.fpsak;

import java.util.function.Supplier;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.LookupResult;
import no.nav.foreldrepenger.oppslag.domain.LookupStatus;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

public class FpsakSupplier implements Supplier<LookupResult<Ytelse>> {

    private final FpsakClient fpsakClient;
    private final AktorId aktor;

    public FpsakSupplier(FpsakClient fpsakClient, AktorId aktor) {
        this.fpsakClient = fpsakClient;
        this.aktor = aktor;
    }

    @Override
    public LookupResult<Ytelse> get() {
        return new LookupResult<>("Fpsak", LookupStatus.SUCCESS, fpsakClient.casesFor(aktor));
    }
}