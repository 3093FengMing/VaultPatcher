package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;

public class Pairs {
    private String lastKey = null;
    private String lastValue = null;
    private final HashMap<String, String> pairs = new HashMap<>();

    public void readJson(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.peek() != JsonToken.END_ARRAY) {
            reader.beginObject();
            while (reader.peek() != JsonToken.END_OBJECT) {
                switch (reader.nextName()) {
                    case "key" : {
                        setKey(reader.nextString());
                        break;
                    }
                    case "value" : {
                        setValue(reader.nextString());
                        break;
                    }
                    default : {
                        reader.skipValue();
                        break;
                    }
                }
            }
            reader.endObject();
        }
        reader.endArray();
    }

//    public void writeJson(JsonWriter writer) throws IOException {
//        writer.beginObject();
//        writer.name("key").value(getKey());
//        writer.name("value").value(getValue());
//        writer.endObject();
//    }


    public HashMap<String, String> getPairs() {
        return pairs;
    }

    public void setKey(String key) {
        if (lastValue == null) {
            // wait value reading
            lastKey = key;
            return;
        }
        pairs.put(key, lastValue);
        lastValue = null;
        lastKey = null;
    }

    public void setValue(String value) {
        if (lastKey == null) {
            // wait key reading
            lastValue = value;
            return;
        }
        pairs.put(lastKey, value);
        lastValue = null;
        lastKey = null;
    }

    public String getValue(String key) {
        return pairs.getOrDefault(key, null);
    }


}