package no.nav.foreldrepenger.mottak.domain.serialization;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.arrayNode;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.booleanValue;
import static no.nav.foreldrepenger.mottak.domain.serialization.JacksonUtils.textValue;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import no.nav.foreldrepenger.mottak.domain.ArbeidsInformasjon;
import no.nav.foreldrepenger.mottak.domain.FramtidigOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.Medlemsskap;
import no.nav.foreldrepenger.mottak.domain.TidligereOppholdsInformasjon;
import no.nav.foreldrepenger.mottak.domain.Utenlandsopphold;

public class MedlemsskapDeserializer extends StdDeserializer<Medlemsskap> {

    private static final Logger LOG = LoggerFactory.getLogger(MedlemsskapSerializer.class);

    public MedlemsskapDeserializer() {
        this(null);
    }

    public MedlemsskapDeserializer(Class<Medlemsskap> medlemsskap) {
        super(medlemsskap);
    }

    @Override
    public Medlemsskap deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        LOG.info("Deserialing");
        JsonNode rootNode = parser.getCodec().readTree(parser);
        return new Medlemsskap(tidligereOpphold(rootNode, parser), framtidigOpphold(rootNode, parser));
    }

    private TidligereOppholdsInformasjon tidligereOpphold(JsonNode rootNode, JsonParser parser) {
        return new TidligereOppholdsInformasjon(norgeSiste12(rootNode), arbeidsInfo(rootNode),
                utenlandsOpphold(rootNode, parser, "utenlandsopphold"));
    }

    private static FramtidigOppholdsInformasjon framtidigOpphold(JsonNode rootNode, JsonParser parser) {
        return new FramtidigOppholdsInformasjon(booleanValue(rootNode, "fødselNorge"),
                booleanValue(rootNode, "norgeNeste12"),
                utenlandsOpphold(rootNode, parser, "framtidigUtenlandsopphold"));
    }

    private static List<Utenlandsopphold> utenlandsOpphold(JsonNode rootNode, JsonParser parser, String nodeName) {
        return utenlandsOpphold(iterator(arrayNode(rootNode, nodeName)), parser);
    }

    private static List<Utenlandsopphold> utenlandsOpphold(Iterator<JsonNode> iterator, JsonParser parser) {
        return StreamSupport
                .stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false)
                .map(s -> utenlandopphold(s, iterator, parser.getCodec()))
                .collect(toList());
    }

    private static Utenlandsopphold utenlandopphold(JsonNode node, Iterator<JsonNode> utland, ObjectCodec codec) {
        try {
            JsonParser parser = ((ObjectNode) utland.next()).traverse();
            parser.setCodec(codec);
            return parser.readValueAs(Utenlandsopphold.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static ArbeidsInformasjon arbeidsInfo(JsonNode rootNode) {
        return ArbeidsInformasjon.valueOf(textValue(rootNode, "arbeidSiste12"));
    }

    private static boolean norgeSiste12(JsonNode rootNode) {
        return booleanValue(rootNode, "norgeSiste12");
    }

    private static Iterator<JsonNode> iterator(ArrayNode utland) {
        return utland != null ? utland.iterator() : Collections.emptyIterator();
    }
}
