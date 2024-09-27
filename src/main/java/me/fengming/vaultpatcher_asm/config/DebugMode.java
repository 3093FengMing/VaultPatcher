package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DebugMode {
    private boolean isEnable = false;
    private int outputMode = 0;
    private String outputFormat = "<source> -> <target> in <class> [<ordinal>] | <info>";
    private int hidePairsLimit = 7;
    private boolean outputNodeDebug = false;
    private boolean exportClass = false;
    private boolean useCache = true;
    private boolean missingWarn = true;

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

    public int getHidePairsLimit() {
        return this.hidePairsLimit;
    }

    public void setHidePairsLimit(int hidePairsLimit) {
        this.hidePairsLimit = hidePairsLimit;
    }

    public boolean isOutputNodeDebug() {
        return this.outputNodeDebug;
    }

    public void setOutputNodeDebug(boolean outputNodeDebug) {
        this.outputNodeDebug = outputNodeDebug;
    }

    public boolean isExportClass() {
        return exportClass;
    }

    public void setExportClass(boolean exportClass) {
        this.exportClass = exportClass;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean updateCache) {
        this.useCache = updateCache;
    }

    public boolean isMissingWarn() {
        return this.missingWarn;
    }

    public void setMissingWarn(boolean missingWarn) {
        this.missingWarn = missingWarn;
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
                case "c":
                case "export_class":
                    setExportClass(reader.nextBoolean());
                    break;
                case "u":
                case "use_cache":
                    setUseCache(reader.nextBoolean());
                    break;
                case "w":
                case "missing_warn":
                    setMissingWarn(reader.nextBoolean());
                    break;
                case "h":
                case "hide_pairs":
                    setHidePairsLimit(reader.nextInt());
                    break;
                case "d":
                case "output_node_debug":
                    setOutputNodeDebug(reader.nextBoolean());
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
        writer.name("hide_pairs").value(getHidePairsLimit());
        writer.name("output_node_debug").value(isOutputNodeDebug());
        writer.name("export_class").value(isExportClass());
        writer.name("use_cache").value(isUseCache());
        writer.name("missing_warn").value(isMissingWarn());
        writer.endObject();
    }
}
