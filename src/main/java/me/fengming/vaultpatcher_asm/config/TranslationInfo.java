package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public class TranslationInfo {
    private final TargetClassInfo targetClassInfo;
    private final Pairs pairs;
    private boolean i18n = false;

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
        writer.name("i18n").value(i18n);
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

    public boolean isI18n() {
        return i18n;
    }

    public void setI18n(boolean i18n) {
        this.i18n = i18n;
    }

    @Override
    public String toString() {
        return "TranslationInfo{" +
                "targetClassInfo=" + targetClassInfo +
                ", pairs=" + pairs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationInfo that = (TranslationInfo) o;
        return Objects.equals(getTargetClassInfo(), that.getTargetClassInfo()) && Objects.equals(getPairs(), that.getPairs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClassInfo(), getPairs());
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
