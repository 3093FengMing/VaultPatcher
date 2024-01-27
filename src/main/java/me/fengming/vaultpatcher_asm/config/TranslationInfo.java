package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class TranslationInfo {
    private final TargetClassInfo targetClassInfo;
    private final Pairs pairs;

    public TranslationInfo(TargetClassInfo targetClassInfo, Pairs pairs) {
        this.targetClassInfo = targetClassInfo;
        this.pairs = pairs;
    }

    public TranslationInfo() {
        this.targetClassInfo = new TargetClassInfo();
        this.pairs = new Pairs();
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "t":
                case "target_class": {
                    getTargetClassInfo().readJson(reader);
                    break;
                }
                case "k":
                case "key": {
                    setKey(reader.nextString());
                    break;
                }
                case "v":
                case "value": {
                    setValue(reader.nextString());
                    break;
                }
                case "p":
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

    public void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("target_class");
        getTargetClassInfo().writeJson(writer);
        writer.name("pairs").beginArray().endArray();
        writer.endObject();
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

    public static class Mutable extends TranslationInfo {
        private TargetClassInfo targetClassInfo;
        private Pairs pairs;

        @Override
        public Pairs getPairs() {
            return this.pairs;
        }

        @Override
        public TargetClassInfo getTargetClassInfo() {
            return this.targetClassInfo;
        }

        public TranslationInfo setPairs(Pairs pairs) {
            this.pairs = pairs;
            return this.toImmutable();
        }

        public TranslationInfo setTargetClassInfo(TargetClassInfo targetClassInfo) {
            this.targetClassInfo = targetClassInfo;
            return this.toImmutable();
        }

        public TranslationInfo toImmutable() {
            return new TranslationInfo(this.targetClassInfo, this.pairs);
        }
    }
}
