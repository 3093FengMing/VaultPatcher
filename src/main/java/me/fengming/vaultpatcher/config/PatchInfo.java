package me.fengming.vaultpatcher.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

public class PatchInfo {
    private String name;
    private String desc;
    private String mods;
    private String authors;

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


    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "name":
                    setName(reader.nextString());
                    break;
                case "desc":
                    setDesc(reader.nextString());
                    break;
                case "mods":
                    setMods(reader.nextString());
                    break;
                case "authors":
                    setAuthors(reader.nextString());
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
        writer.name("patch_info");
        writer.name("name").value(getName());
        writer.name("desc").value(getDesc());
        writer.name("mods").value(getMods());
        writer.name("authors").value(getAuthors());
        writer.endObject();
    }
}
