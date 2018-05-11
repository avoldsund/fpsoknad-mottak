package no.nav.foreldrepenger.mottak.domain.felles;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Opphold;

@Data
@Valid
@JsonPropertyOrder({ "fødselNorge", "norgeNeste12", "utenlandsOpphold" })
public class FramtidigOppholdsInformasjon {

    private final boolean fødselNorge;
    private final boolean norgeNeste12;
    @Opphold(fortid = false)
    private final List<Utenlandsopphold> utenlandsOpphold;

    @JsonCreator
    public FramtidigOppholdsInformasjon(@JsonProperty("fødseINorge") boolean fødselNorge,
            @JsonProperty("norgeNeste12") boolean norgeNeste12,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.fødselNorge = fødselNorge;
        this.norgeNeste12 = norgeNeste12;
        this.utenlandsOpphold = utenlandsOpphold == null ? Collections.emptyList() : utenlandsOpphold;
    }
}