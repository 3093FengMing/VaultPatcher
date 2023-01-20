package me.fengming.vaultpatcher.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public class DebugMode {
    private boolean isEnable = false;

    private int outputMode = 0;

    private String outputFormat = "%s -> %s";

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public int getOutputMode() {
        return outputMode;
    }

    public void setOutputMode(int outputMode) {
        this.outputMode = outputMode;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "is_enable" : setEnable(reader.nextBoolean()); break;
                case "output_format" : setOutputFormat(reader.nextString()); break;
                case "output_mode" : setOutputMode(reader.nextInt()); break;
                default : reader.skipValue(); break;
            }
        }
        reader.endObject();
    }

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("is_enable").value(isEnable());
        writer.name("output_format").value(getOutputFormat());
        writer.name("output_mode").value(getOutputMode());
        writer.endObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DebugMode debugMode)) return false;
        return isEnable() == debugMode.isEnable() && getOutputMode() == debugMode.getOutputMode() && Objects.equals(getOutputFormat(), debugMode.getOutputFormat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isEnable(), getOutputMode(), getOutputFormat());
    }

    @Override
    public String toString() {
        return "DebugMode{" +
                "isEnable=" + isEnable +
                ", outputMode=" + outputMode +
                ", outputFormat='" + outputFormat + '\'' +
                '}';
    }
}
