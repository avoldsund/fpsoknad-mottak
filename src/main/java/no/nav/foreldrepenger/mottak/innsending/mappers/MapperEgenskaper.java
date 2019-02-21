package no.nav.foreldrepenger.mottak.innsending.mappers;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Versjon.DEFAULT_VERSJON;

import java.util.Arrays;
import java.util.List;

import no.nav.foreldrepenger.mottak.innsending.SøknadType;
import no.nav.foreldrepenger.mottak.innsyn.SøknadEgenskap;
import no.nav.foreldrepenger.mottak.util.Versjon;

public class MapperEgenskaper {
    private final List<SøknadEgenskap> egenskaper;

    public MapperEgenskaper(Versjon versjon, SøknadType type) {
        this(new SøknadEgenskap(versjon, type));
    }

    public MapperEgenskaper(SøknadType... typer) {
        this(DEFAULT_VERSJON, typer);
    }

    public MapperEgenskaper(Versjon versjon, SøknadType... typer) {
        this(typerForVersjon(versjon, typer));
    }

    public MapperEgenskaper(SøknadEgenskap egenskap) {
        this(singletonList(egenskap));
    }

    public MapperEgenskaper(List<SøknadEgenskap> egenskaper) {
        this.egenskaper = egenskaper;
    }

    public List<SøknadEgenskap> getEgenskaper() {
        return egenskaper;
    }

    public boolean kanMappe(SøknadEgenskap egenskap) {
        return egenskaper.contains(egenskap);
    }

    private static List<SøknadEgenskap> typerForVersjon(final Versjon versjon, SøknadType... typer) {
        return Arrays.stream(typer)
                .map(type -> new SøknadEgenskap(versjon, type))
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mapperEgenskaper=" + egenskaper + "]";
    }

}