package no.nav.foreldrepenger.mottak.domain.foreldrepenger;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

public class UtenlandskOrganisasjon extends EgenNæring {

    @Length(max = 50)
    private String orgName;
    private CountryCode registrertLand;

    @Builder
    private UtenlandskOrganisasjon(CountryCode arbeidsland, List<Virksomhetstype> virksomhetsTyper, ÅpenPeriode periode,
            String beskrivelseRelasjon, Regnskapsfører regnskapsfører, boolean erNyOpprettet, boolean erVarigEndring,
            long næringsinntektBrutto, LocalDate endringsDato, String beskrivelseEndring, List<String> vedlegg,
            String orgName, CountryCode registrertLand) {
        super(arbeidsland, virksomhetsTyper, periode, beskrivelseRelasjon, regnskapsfører, erNyOpprettet,
                erVarigEndring,
                næringsinntektBrutto, endringsDato, beskrivelseEndring, vedlegg);
        this.orgName = orgName;
        this.registrertLand = registrertLand;
    }

}