package no.nav.foreldrepenger.mottak.innsyn.dto;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.mottak.util.StreamUtil.safeStream;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.mottak.innsyn.Lenke;

public class BehandlingDTO {

    private static final String SØKNAD = "søknad";
    private final String status;
    private final String type;
    private final String tema;
    private final String årsak;
    private final String behandlendeEnhet;
    private final String behandlendeEnhetNavn;
    private final String behandlingResultatType;
    private final LocalDateTime opprettetTidspunkt;
    private final LocalDateTime endretTidspunkt;
    private final List<String> inntekstmeldinger;
    private final List<Lenke> lenker;

    @JsonCreator
    public BehandlingDTO(
            @JsonProperty("opprettetTidspunkt") LocalDateTime opprettetTidspunkt,
            @JsonProperty("endretTidspunkt") LocalDateTime endretTidspunkt,
            @JsonProperty("status") String status,
            @JsonProperty("type") String type,
            @JsonProperty("tema") String tema,
            @JsonProperty("årsak") String årsak,
            @JsonProperty("behandlendeEnhet") String behandlendeEnhet,
            @JsonProperty("behandlendeEnhetNavn") String behandlendeEnhetNavn,
            @JsonProperty("behandlingResultatType") String behandlingResultatType,
            @JsonProperty("inntekstmeldinger") List<String> inntekstmeldinger,
            @JsonProperty("lenker") List<Lenke> lenker) {
        this.opprettetTidspunkt = opprettetTidspunkt;
        this.endretTidspunkt = endretTidspunkt;
        this.status = status;
        this.tema = tema;
        this.type = type;
        this.årsak = årsak;
        this.behandlendeEnhet = behandlendeEnhet;
        this.behandlendeEnhetNavn = behandlendeEnhetNavn;
        this.behandlingResultatType = behandlingResultatType;
        this.inntekstmeldinger = Optional.ofNullable(inntekstmeldinger).orElse(emptyList());
        this.lenker = Optional.ofNullable(lenker).orElse(emptyList());
    }

    public static String getSøknad() {
        return SØKNAD;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getTema() {
        return tema;
    }

    public String getÅrsak() {
        return årsak;
    }

    public String getBehandlendeEnhet() {
        return behandlendeEnhet;
    }

    public String getBehandlendeEnhetNavn() {
        return behandlendeEnhetNavn;
    }

    public String getBehandlingResultatType() {
        return behandlingResultatType;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }

    public List<String> getInntekstmeldinger() {
        return inntekstmeldinger;
    }

    public List<Lenke> getLenker() {
        return lenker;
    }

    public Lenke getSøknadsLenke() {
        return safeStream(getLenker())
                .filter(s -> s.getRel().equals(SØKNAD))
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [status=" + status + ", type=" + type + ", tema=" + tema + ", årsak="
                + årsak + ", behandlendeEnhet=" + behandlendeEnhet + ", behandlendeEnhetNavn=" + behandlendeEnhetNavn
                + ", behandlingResultatType=" + behandlingResultatType + ", opprettetTidspunkt=" + opprettetTidspunkt
                + ", endretTidspunkt=" + endretTidspunkt + ", inntekstmeldinger=" + inntekstmeldinger + ", lenker="
                + lenker + "]";
    }
}