package no.nav.foreldrepenger.mottak.domain;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class MedlemsskapDeserializer extends StdDeserializer<Medlemsskap> {

    public MedlemsskapDeserializer() {
        this(null);
    }

    public MedlemsskapDeserializer(Class<Medlemsskap> t) {
        super(t);
    }

    @Override
    public Medlemsskap deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException, JsonProcessingException {
        JsonNode rootNode = p.getCodec().readTree(p);
        return new Medlemsskap(tidligereOpphold(rootNode, p.getCodec()), fremtidigOpphold(rootNode, p.getCodec()));
    }

    private TidligereOppholdsInformasjon tidligereOpphold(JsonNode rootNode, ObjectCodec codec) {
        ArrayNode utland = (ArrayNode) rootNode.get("utenlandsopphold");
        return new TidligereOppholdsInformasjon(booleanValue(rootNode, "iNorgeSiste12"),
                ArbeidsInformasjon.valueOf(textValue(rootNode, "arbeidSiste12")),
                utenlandsOpphold(utland, codec));
    }

    private static FramtidigOppholdsInformasjon fremtidigOpphold(JsonNode rootNode, ObjectCodec codec) {
        ArrayNode utland = (ArrayNode) rootNode.get("fremtidigUtenlandsopphold");
        return new FramtidigOppholdsInformasjon(booleanValue(rootNode, "fødselINorge"),
                utenlandsOpphold(utland, codec));
    }

    private static String textValue(JsonNode rootNode, String fieldName) {
        return ((TextNode) rootNode.get(fieldName)).textValue();
    }

    private static boolean booleanValue(JsonNode rootNode, String fieldName) {
        return ((BooleanNode) rootNode.get(fieldName)).booleanValue();
    }

    private static List<Utenlandsopphold> utenlandsOpphold(ArrayNode utland, ObjectCodec codec) {
        Iterator<JsonNode> iterator = iterator(utland);
        return StreamSupport
                .stream(((Iterable<JsonNode>) () -> iterator).spliterator(), false)
                .map(s -> utenlandopphold(s, iterator, codec))
                .collect(Collectors.toList());

    }

    private static Iterator<JsonNode> iterator(ArrayNode utland) {
        return utland != null ? utland.iterator() : Collections.emptyIterator();
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

}
