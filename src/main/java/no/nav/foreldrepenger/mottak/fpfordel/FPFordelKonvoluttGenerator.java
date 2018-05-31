package no.nav.foreldrepenger.mottak.fpfordel;

import static org.springframework.http.HttpHeaders.CONTENT_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import no.nav.foreldrepenger.mottak.domain.AktorId;
import no.nav.foreldrepenger.mottak.domain.Søknad;
import no.nav.foreldrepenger.mottak.domain.felles.Vedlegg;
import no.nav.foreldrepenger.mottak.domain.foreldrepenger.Ettersending;
import no.nav.foreldrepenger.mottak.pdf.ForeldrepengerPDFGenerator;

@Component
public class FPFordelKonvoluttGenerator {

    public static final String VEDLEGG = "vedlegg";
    public static final String METADATA = "metadata";
    public static final String CONTENT_ID = "Content-ID";
    public static final String HOVEDDOKUMENT = "hoveddokument";
    private final FPFordelMetdataGenerator metadataGenerator;
    private final FPFordelSøknadGenerator søknadGenerator;
    private final ForeldrepengerPDFGenerator pdfGenerator;

    public FPFordelKonvoluttGenerator(FPFordelMetdataGenerator metadataGenerator,
            FPFordelSøknadGenerator søknadGenerator, ForeldrepengerPDFGenerator pdfGenerator) {
        this.metadataGenerator = metadataGenerator;
        this.søknadGenerator = søknadGenerator;
        this.pdfGenerator = pdfGenerator;
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Søknad søknad, AktorId aktørId, String ref) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        AtomicInteger id = new AtomicInteger(1);
        builder.part(METADATA, metadata(søknad, aktørId, ref), APPLICATION_JSON_UTF8);
        builder.part(HOVEDDOKUMENT, xmlHovedDokument(søknad, aktørId), APPLICATION_XML).header(CONTENT_ID, id(id));
        builder.part(HOVEDDOKUMENT, pdfHovedDokument(søknad), APPLICATION_PDF)
                .header(CONTENT_ID, id(id))
                .header(CONTENT_ENCODING, "base64");
        søknad.getVedlegg().stream()
                .forEach(vedlegg -> addVedlegg(builder, vedlegg, id));

        return new HttpEntity<>(builder.build(), headers());
    }

    public HttpEntity<MultiValueMap<String, HttpEntity<?>>> payload(Ettersending ettersending, AktorId aktørId,
            String ref) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part(METADATA, metadata(ettersending, aktørId, ref), APPLICATION_JSON_UTF8);
        return new HttpEntity<>(builder.build(), headers());
    }

    private static void addVedlegg(MultipartBodyBuilder builder, Vedlegg vedlegg, AtomicInteger id) {
        builder.part(VEDLEGG, encode(vedlegg.getVedlegg()), APPLICATION_PDF)
                .headers(headers(vedlegg));
    }

    private static VedleggHeaderConsumer headers(Vedlegg vedlegg) {
        return new VedleggHeaderConsumer(vedlegg.getMetadata().getBeskrivelse());
    }

    private static String id(AtomicInteger id) {
        return String.valueOf(id.getAndIncrement());
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MULTIPART_FORM_DATA);
        return headers;
    }

    private String metadata(Søknad søknad, AktorId aktørId, String ref) {
        return metadataGenerator.generateMetadata(new FPFordelMetadata(søknad, aktørId, ref));
    }

    private String metadata(Ettersending ettersending, AktorId aktørId, String ref) {
        return metadataGenerator.generateMetadata(new FPFordelMetadata(ettersending, aktørId, ref));
    }

    private byte[] pdfHovedDokument(Søknad søknad) {
        return encode(pdfGenerator.generate(søknad));
    }

    private String xmlHovedDokument(Søknad søknad, AktorId aktørId) {
        return søknadGenerator.toXML(søknad, aktørId);
    }

    private static byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [metadataGenerator=" + metadataGenerator + ", søknadGenerator="
                + søknadGenerator + ", pdfGenerator=" + pdfGenerator + "]";
    }

    private static final class VedleggHeaderConsumer implements Consumer<HttpHeaders> {
        private final String filNavn;

        private VedleggHeaderConsumer(String filNavn) {
            this.filNavn = filNavn;
        }

        @Override
        public void accept(HttpHeaders headers) {
            headers.setContentDispositionFormData(VEDLEGG, filNavn);
        }
    }

}
