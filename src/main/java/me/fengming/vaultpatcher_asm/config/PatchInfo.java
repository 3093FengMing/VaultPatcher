package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class PatchInfo {
    private String infoName = "Unknown";
    private String infoDesc = "No description";
    private String infoMods = "Unknown";
    private String infoAuthors = "Unknown";
    private boolean dataDynamic = false;
    private boolean dataI18n = false;

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoDesc() {
        return infoDesc;
    }

    public void setInfoDesc(String infoDesc) {
        this.infoDesc = infoDesc;
    }

    public String getInfoMods() {
        return infoMods;
    }

    public void setInfoMods(String infoMods) {
        this.infoMods = infoMods;
    }

    public String getInfoAuthors() {
        return infoAuthors;
    }

    public void setInfoAuthors(String infoAuthors) {
        this.infoAuthors = infoAuthors;
    }

    public boolean isDataDynamic() {
        return dataDynamic;
    }

    public void setDataDynamic(boolean dataDynamic) {
        this.dataDynamic = dataDynamic;
    }

    public boolean isDataI18n() {
        return this.dataI18n;
    }

    public void setDataI18n(boolean dataI18n) {
        this.dataI18n = dataI18n;
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "n":
                case "name": {
                    setInfoName(reader.nextString());
                    break;
                }
                case "d":
                case "desc": {
                    setInfoDesc(reader.nextString());
                    break;
                }
                case "m":
                case "mods": {
                    setInfoMods(reader.nextString());
                    break;
                }
                case "a":
                case "authors": {
                    setInfoAuthors(reader.nextString());
                    break;
                }
                case "y":
                case "dyn":
                case "dynamic": {
                    setDataDynamic(reader.nextBoolean());
                    break;
                }
                case "i":
                case "i18n": {
                    setDataI18n(reader.nextBoolean());
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

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("name").value("Here is name of your module");
        writer.name("desc").value("You can describe the module here.");
        writer.name("mods").value("And this is the applied mod of your module");
        writer.name("authors").value("You can write down your name here");
        writer.name("dynamic").value(false);
        writer.name("i18n").value(false);
        writer.endObject();
    }
}
