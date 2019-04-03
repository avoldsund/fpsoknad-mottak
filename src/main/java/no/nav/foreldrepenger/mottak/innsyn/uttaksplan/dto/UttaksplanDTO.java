package no.nav.foreldrepenger.mottak.innsyn.uttaksplan.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.FamiliehendelseType;
import no.nav.foreldrepenger.mottak.innsyn.uttaksplan.UttaksPeriode;

@Data
public class UttaksplanDTO {

    private final FamiliehendelseType familieHendelseType;
    private final LocalDate familieHendelseDato;

    private final Dekningsgrad dekningsgrad;
    private final Integer antallBarn;

    private final Boolean søkerErFarEllerMedmor;
    private final Boolean morErAleneOmOmsorg;
    private final Boolean morHarRett;
    private final Boolean morErUfør;

    private final Boolean farMedmorErAleneOmOmsorg;
    private final Boolean farMedmorHarRett;
    private final List<UttaksPeriode> uttaksPerioder;

    @JsonCreator
    public UttaksplanDTO(@JsonProperty("familieHendelseType") FamiliehendelseType familieHendelseType,
            @JsonProperty("familieHendelseDato") LocalDate familieHendelseDato,
            @JsonProperty("dekningsgrad") Dekningsgrad dekningsgrad,
            @JsonProperty("antallBarn") Integer antallBarn,
            @JsonProperty("søkerErFarEllerMedmor") Boolean søkerErFarEllerMedmor,
            @JsonProperty("morErAleneOmOmsorg") Boolean morErAleneOmOmsorg,
            @JsonProperty("morHarRett") Boolean morHarRett,
            @JsonProperty("morErUfør") Boolean morErUfør,
            @JsonProperty("farMedmorErAleneOmOmsorg") Boolean farMedmorErAleneOmOmsorg,
            @JsonProperty("farMedmorHarRett") Boolean farMedmorHarRett,
            @JsonProperty("uttaksPerioder") List<UttaksPeriode> uttaksPerioder) {
        this.familieHendelseType = familieHendelseType;
        this.familieHendelseDato = familieHendelseDato;
        this.dekningsgrad = dekningsgrad;
        this.antallBarn = antallBarn;
        this.søkerErFarEllerMedmor = søkerErFarEllerMedmor;
        this.morErAleneOmOmsorg = morErAleneOmOmsorg;
        this.morHarRett = morHarRett;
        this.morErUfør = morErUfør;
        this.farMedmorErAleneOmOmsorg = farMedmorErAleneOmOmsorg;
        this.farMedmorHarRett = farMedmorHarRett;
        this.uttaksPerioder = uttaksPerioder;
    }
}
