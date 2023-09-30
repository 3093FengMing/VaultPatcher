package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DebugMode {
    private boolean isEnable = false;

    private int outputMode = 0;

    private String outputFormat = "<source> -> <target>";


    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
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
                case "e":
                case "is_enable":
                    setEnable(reader.nextBoolean());
                    break;
                case "f":
                case "output_format":
                    setOutputFormat(reader.nextString());
                    break;
                case "m":
                case "output_mode":
                    setOutputMode(reader.nextInt());
                    break;
                default:
                    reader.skipValue();
                    break;
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
}
