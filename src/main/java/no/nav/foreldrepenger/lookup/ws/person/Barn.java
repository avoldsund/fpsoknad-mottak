package no.nav.foreldrepenger.lookup.ws.person;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDate;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Barn {

    private final Fødselsnummer fnr;
    private final Fødselsnummer fnrSøker;
    private final LocalDate fødselsdato;
    private final Navn navn;
    private final Kjønn kjønn;
    private final AnnenForelder annenForelder;

    public Barn(Fødselsnummer fnrSøker, Fødselsnummer fnr, LocalDate fødselsdato, Navn navn, Kjønn kjønn, AnnenForelder annenForelder) {
        this.fnr = fnr;
        this.fnrSøker = fnrSøker;
        this.fødselsdato = requireNonNull(fødselsdato);
        this.navn = navn;
        this.kjønn = kjønn;
        this.annenForelder = annenForelder;
    }

    @JsonUnwrapped
    public Fødselsnummer getFnr() {
        return fnr;
    }

    @JsonUnwrapped
    public Fødselsnummer getFnrSøker() {
        return fnrSøker;
    }

    @JsonUnwrapped
    public Navn getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public Kjønn getKjønn() {
        return kjønn;
    }

    public AnnenForelder getAnnenForelder() {
        return annenForelder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnr, fødselsdato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Barn that = (Barn) o;
        return Objects.equals(fnr, that.fnr) && Objects.equals(fødselsdato, that.fødselsdato);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnr + "]";
    }
}