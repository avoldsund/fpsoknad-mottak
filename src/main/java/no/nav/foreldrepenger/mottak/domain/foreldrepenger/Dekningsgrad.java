package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Dekningsgrad {

    GRAD80("80"), GRAD100("100");

    private final String kode;

    Dekningsgrad(String kode) {
        this.kode = kode;
    }

    @JsonValue
    public String kode() {
        return kode;
    }

    public static Dekningsgrad fraKode(String kode) {
        return Arrays.stream(Dekningsgrad.values())
                .filter(e -> e.kode.equals(kode))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Ikke støttet dekningsgrad %s.", kode)));
    }

}
