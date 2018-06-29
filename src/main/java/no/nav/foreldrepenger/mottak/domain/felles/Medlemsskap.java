package no.nav.foreldrepenger.mottak.domain.felles;

import java.time.LocalDate;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "tidligereOppholdsInfo", "framtidigOppholdsInfo" })
public class Medlemsskap {

    @Valid
    private final TidligereOppholdsInformasjon tidligereOppholdsInfo;
    @Valid
    private final FramtidigOppholdsInformasjon framtidigOppholdsInfo;

    public boolean varUtenlands(LocalDate day) {
        return tidligereOppholdsInfo.varUtenlands(day);
    }
}