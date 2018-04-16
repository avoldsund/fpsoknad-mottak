package no.nav.foreldrepenger.mottak.domain;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.validation.Periode;

@Data
@Periode
@JsonPropertyOrder({ "fom", "tom" })
public class LukketPeriode {

    private static final Logger LOG = LoggerFactory.getLogger(LukketPeriode.class);

    @NotNull
    private final LocalDate fom;
    @NotNull
    private final LocalDate tom;

    @JsonCreator
    public LukketPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
        validate(fom, tom);
    }

    public boolean overlapper(LukketPeriode annenPeriode) {
        LOG.info("Sammeligner {} med {}", this, annenPeriode);
        if (annenPeriode.getFom().isAfter(this.getTom())) {
            LOG.info("Periodene overlapper ikke");
            return false;
        }
        if (annenPeriode.getTom().isBefore(this.getFom())) {
            LOG.info("Periodene overlapper ikke");
            return false;
        }
        LOG.info("Periodene overlapper");
        return true;
    }

    private static void validate(LocalDate fom, LocalDate tom) {
        if (fom.isAfter(tom)) {
            throw new IllegalStateException("Startdato må være tiligere enn sluttdato");
        }
    }
}