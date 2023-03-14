package me.fengming.vaultpatcher.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OptimizeParams {
    private int stackMin = -1;
    private int stackMax = -1;
    private boolean disableExport = false;

    public boolean isDisableExport() {
        return disableExport;
    }

    public int getStackMin() {
        return stackMin;
    }

    public int getStackMax() {
        return stackMax;
    }

    public void setDisableExport(boolean disableExport) {
        this.disableExport = disableExport;
    }

    public void setStackMin(int stackMin) {
        this.stackMin = stackMin;
    }

    public void setStackMax(int stackMax) {
        this.stackMax = stackMax;
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "disable_export":
                    setDisableExport(reader.nextBoolean());
                    break;
                case "stack_min":
                    setStackMin(reader.nextInt());
                    break;
                case "stack_max":
                    setStackMax(reader.nextInt());
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
        writer.name("disable_export").value(isDisableExport());
        writer.name("stack_min").value(getStackMin());
        writer.name("stack_max").value(getStackMax());
        writer.endObject();
    }
}
