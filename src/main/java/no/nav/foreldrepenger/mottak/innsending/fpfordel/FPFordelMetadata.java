package no.nav.foreldrepenger.mottak.innsending.fpfordel;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.felles.VedleggSkjemanummer.SØKNAD_FOELDREPEMGER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.innsending.fpfordel.FPFordelMetdataGenerator.Files;

@JsonPropertyOrder({ "forsendelsesId", "brukerId", "forsendelseMottatt", "filer" })
public class FPFordelMetadata {
    private final String forsendelsesId;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime forsendelseMottatt;
    private final String brukerId;
    private final List<Files> filer;
    private final String saksNr;

    public FPFordelMetadata(Ettersending ettersending, AktorId aktorId, String ref) {
        this(files(ettersending), aktorId, ref, ettersending.getSaksnr());
    }

    public String getSaksNr() {
        return saksNr;
    }

    public FPFordelMetadata(Søknad søknad, AktorId aktorId, String ref) {
        this(files(søknad), aktorId, ref, null);
    }

    public FPFordelMetadata(List<Files> filer, AktorId aktorId, String ref, String saksnr) {
        this.forsendelsesId = ref;
        this.brukerId = aktorId.getId();
        this.forsendelseMottatt = LocalDateTime.now();
        this.filer = filer;
        this.saksNr = saksnr;
    }

    public List<Files> getFiler() {
        return filer;
    }

    public String getForsendelsesId() {
        return forsendelsesId;
    }

    public LocalDateTime getForsendelseMottatt() {
        return forsendelseMottatt;
    }

    public String getBrukerId() {
        return brukerId;
    }

    private static List<Files> files(Søknad søknad) {
        final AtomicInteger id = new AtomicInteger(1);
        List<Files> dokumenter = newArrayList(søknad(id), søknad(id));
        dokumenter.addAll(søknad.getVedlegg().stream()
                .map(s -> vedlegg(s, id))
                .collect(toList()));
        return dokumenter;
    }

    private static List<Files> files(Ettersending ettersending) {
        AtomicInteger id = new AtomicInteger(1);
        return ettersending.getVedlegg().stream().map(s -> vedlegg(s, id)).collect(toList());

    }

    private static Files søknad(final AtomicInteger id) {
        return new Files(SØKNAD_FOELDREPEMGER, id.getAndIncrement());
    }

    private static Files vedlegg(Vedlegg vedlegg, final AtomicInteger id) {
        return new Files(vedlegg.getMetadata().getSkjemanummer(), id.getAndIncrement());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [forsendelsesId=" + forsendelsesId + ", forsendelseMottatt="
                + forsendelseMottatt + ", brukerId=" + brukerId + ", filer=" + filer + "]";
    }
}