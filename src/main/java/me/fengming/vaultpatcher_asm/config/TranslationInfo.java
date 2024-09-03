package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

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
