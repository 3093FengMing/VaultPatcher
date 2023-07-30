package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

public class TranslationInfo {
    private final TargetClassInfo targetClassInfo = new TargetClassInfo();
    private String key;
    private String value;

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "target_class" -> getTargetClassInfo().readJson(reader);
                case "key" -> setKey(reader.nextString());
                case "value" -> setValue(reader.nextString());
                default -> reader.skipValue();
            }
        }
        reader.endObject();
    }


    public TargetClassInfo getTargetClassInfo() {
        return targetClassInfo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
