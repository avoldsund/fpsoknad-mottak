package no.nav.foreldrepenger.mottak.innsending.foreldrepenger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.util.Versjon.ALL;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Endringssøknad;
import no.nav.foreldrepenger.mottak.http.errorhandling.UnsupportedVersionException;
import no.nav.foreldrepenger.mottak.util.Versjon;

@Component
public class DelegerendeDomainMapper implements VersjonsBevisstDomainMapper {

    private final List<DomainMapper> mappers;

    public DelegerendeDomainMapper(DomainMapper... mappers) {
        this(asList(mappers));
    }

    @Inject
    public DelegerendeDomainMapper(List<DomainMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public Versjon versjon() {
        return ALL;
    }

    @Override
    public List<SøknadType> typer() {
        return mappers.stream()
                .map(m -> m.typer())
                .flatMap(s -> s.stream())
                .collect(toList());
    }

    @Override
    public String tilXML(Søknad søknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(søknad, søker);
    }

    @Override
    public String tilXML(Endringssøknad endringssøknad, AktorId søker, Versjon versjon) {
        return mapper(versjon).tilXML(endringssøknad, søker);

    }

    private DomainMapper mapper(Versjon versjon) {
        return mappers.stream()
                .filter(s -> s.versjon().equals(versjon))
                .findFirst()
                .orElseThrow(() -> new UnsupportedVersionException("Versjon " + versjon + " ikke støttet", versjon));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [mappers=" + mappers + "]";
    }

}