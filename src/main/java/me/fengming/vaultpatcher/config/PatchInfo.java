package me.fengming.vaultpatcher.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

public class PatchInfo {
    private final PatchInfo patchInfo = new PatchInfo();
    private String name;
    private String desc;
    private String mods;
    private String authors;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getMods() {
        return mods;
    }

    public String getAuthors() {
        return authors;
    }

    public PatchInfo getPatchInfo() {
        return patchInfo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMods(String mods) {
        this.mods = mods;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }


    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "patch_info" -> getPatchInfo().readJson(reader);
                case "name" -> setName(reader.nextString());
                case "desc" -> setDesc(reader.nextString());
                case "mods" -> setMods(reader.nextString());
                case "authors" -> setAuthors(reader.nextString());
                default -> reader.skipValue();
            }
        }
        reader.endObject();
    }

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("patch_info");
        getPatchInfo().writeJson(writer);
        writer.name("name").value(getName());
        writer.name("desc").value(getDesc());
        writer.name("mods").value(getMods());
        writer.name("authors").value(getAuthors());
        writer.endObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PatchInfo that = (PatchInfo) o;

        return new EqualsBuilder()
                .append(patchInfo, that.patchInfo)
                .append(name, that.name)
                .append(desc, that.desc)
                .append(mods, that.mods)
                .append(authors, that.authors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 53)
                .append(patchInfo)
                .append(name)
                .append(desc)
                .append(mods)
                .append(authors)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("patchInfo", patchInfo)
                .append("name", name)
                .append("desc", desc)
                .append("mods", mods)
                .append("authors", authors)
                .toString();
    }
}
