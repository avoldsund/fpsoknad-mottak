package no.nav.foreldrepenger.mottak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class AktorId {
    @JsonValue
    private final String value;

    @JsonCreator
    public AktorId(@JsonProperty("value") String value) {
        this.value = value;
    }

    public static AktorId valueOf(String value) {
        return new AktorId(value);
    }

}
