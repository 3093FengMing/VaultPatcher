package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

public class TranslationInfo {
    private final TargetClassInfo targetClassInfo = new TargetClassInfo();
    private final Pairs pairs = new Pairs();

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "target_class":
                case "target": {
                    getTargetClassInfo().readJson(reader);
                    break;
                }
                case "key": {
                    setKey(reader.nextString());
                    break;
                }
                case "value": {
                    setValue(reader.nextString());
                    break;
                }
                case "pairs": {
                    getPairs().readJson(reader);
                    break;
                }
                default: {
                    reader.skipValue();
                    break;
                }
            }
        }
        reader.endObject();
    }


    public TargetClassInfo getTargetClassInfo() {
        return targetClassInfo;
    }

    public Pairs getPairs() {
        return pairs;
    }

    public void setKey(String key) {
        pairs.setKey(key);
    }

    public void setValue(String value) {
        pairs.setValue(value);
    }

    @Override
    public String toString() {
        return "TranslationInfo{" +
                "targetClassInfo=" + targetClassInfo +
                ", pairs=" + pairs +
                '}';
    }
}
