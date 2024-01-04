package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class PatchInfo {
    private String name;
    private String desc;
    private String mods;
    private String authors;
    private boolean dynamic = false;

    public String getName() {
        return name == null || name.isEmpty() ? "Unknown" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc == null || desc.isEmpty() ? "No description" : desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMods() {
        return mods == null || mods.isEmpty() ? "Unknown" : mods;
    }

    public void setMods(String mods) {
        this.mods = mods;
    }

    public String getAuthors() {
        return authors == null || authors.isEmpty() ? "Unknown" : authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }


    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "n":
                case "name": {
                    setName(reader.nextString());
                    break;
                }
                case "d":
                case "desc": {
                    setDesc(reader.nextString());
                    break;
                }
                case "m":
                case "mods": {
                    setMods(reader.nextString());
                    break;
                }
                case "a":
                case "authors": {
                    setAuthors(reader.nextString());
                    break;
                }
                case "e":
                case "dyn":
                case "dynamic": {
                    setDynamic(reader.nextBoolean());
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
        writer.endObject();
    }
}
