package no.nav.foreldrepenger.mottak.domain.felles;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.annotations.Prosent;

@Data
public class ProsentAndel {

    @Prosent
    private final Double prosent;

    @JsonCreator
    public ProsentAndel(@JsonProperty("prosent") Double prosent) {
        this.prosent = round(prosent, 1);
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
