package org.game.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

public class PairDeserializer<K, V> extends JsonDeserializer<Pair<K, V>> {

    @Override
    public Pair<K, V> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        Iterator<Entry<String, JsonNode>> fields = node.fields();
        if (fields.hasNext()) {
            Entry<String, JsonNode> entry = fields.next();
            K key = (K) entry.getKey(); // convertir manualmente si es necesario
            V value = (V) p.getCodec().treeToValue(entry.getValue(), Object.class);
            return new Pair<>(key, value);
        }

        return null;
    }
}
