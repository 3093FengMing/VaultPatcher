package me.fengming.vaultpatcher.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("target_class");
        getTargetClassInfo().writeJson(writer);
        writer.name("key").value(getKey());
        writer.name("value").value(getValue());
        writer.endObject();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TranslationInfo that = (TranslationInfo) o;

        return new EqualsBuilder()
                .append(targetClassInfo, that.targetClassInfo)
                .append(key, that.key)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(targetClassInfo)
                .append(key)
                .append(value)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("targetClassInfo", targetClassInfo)
                .append("key", key)
                .append("value", value)
                .toString();
    }
}
