package no.nav.foreldrepenger.mottak.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Søker {
    @NotNull(message = "{ytelse.fnr.notnull}")
    private final Fødselsnummer fnr;
    @NotNull(message = "{ytelse.aktør.notnull}")
    private final AktorId aktør;
    @NotNull(message = "{ytelse.søknadsrolle.notnull}")
    private final BrukerRolle søknadsRolle;
    private final Navn navn;

    @JsonCreator
    public Søker(@JsonProperty("fnr") Fødselsnummer fnr, @JsonProperty("aktør") AktorId aktør,
            @JsonProperty("søknadsRolle") BrukerRolle søknadsRolle, @JsonProperty("navn") Navn navn) {
        this.fnr = fnr;
        this.aktør = aktør;
        this.søknadsRolle = søknadsRolle;
        this.navn = navn;

    }

}
