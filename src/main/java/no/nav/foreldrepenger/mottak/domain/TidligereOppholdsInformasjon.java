package no.nav.foreldrepenger.mottak.domain;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TidligereOppholdsInformasjon {

    private final boolean boddINorge;
    private final ArbeidsInformasjon arbeidsInfo;
    @Valid
    private final List<Utenlandsopphold> utenlandsOpphold;

    public TidligereOppholdsInformasjon() {
        this(true, ArbeidsInformasjon.ARBEIDET_I_NORGE, Collections.emptyList());
    }

    public TidligereOppholdsInformasjon(boolean boddINorge, ArbeidsInformasjon arbeidsInfo,
            Utenlandsopphold utenlandsOpphold) {
        this(boddINorge, arbeidsInfo, Collections.singletonList(utenlandsOpphold));
    }

    @JsonCreator
    public TidligereOppholdsInformasjon(@JsonProperty("boddINorge") boolean boddINorge,
            @JsonProperty("arbeidsInfo") ArbeidsInformasjon arbeidsInfo,
            @JsonProperty("utenlandsOpphold") List<Utenlandsopphold> utenlandsOpphold) {
        this.boddINorge = boddINorge;
        this.arbeidsInfo = arbeidsInfo;
        this.utenlandsOpphold = utenlandsOpphold != null ? utenlandsOpphold : Collections.emptyList();
    }
}
