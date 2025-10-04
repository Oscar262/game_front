package org.game.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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

        if (node.has("first") && node.has("second")) {
            K key = (K) p.getCodec().treeToValue(node.get("first"), Object.class);
            V value = (V) p.getCodec().treeToValue(node.get("second"), Object.class);
            return new Pair<>(key, value);
        }

        return null; // o new Pair<>(null, null) si prefieres no retornar null
    }
}
