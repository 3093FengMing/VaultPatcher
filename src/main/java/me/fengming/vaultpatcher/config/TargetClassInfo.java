package me.fengming.vaultpatcher.config;

import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

public class TargetClassInfo {
    private String name = "";
    private String mapping = "SRG";
    private int stackDepth = -1;

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "name" : setName(reader.nextString()); break;
                case "mapping" : setMapping(reader.nextString()); break;
                case "stack_depth" : setStackDepth(reader.nextInt()); break;
                default : reader.skipValue(); break;
            }
        }
        reader.endObject();
    }

    public void writeJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("name").value(getName());
        writer.name("mapping").value(getMapping());
        writer.name("stack_depth").value(getStackDepth());
        writer.endObject();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        // Compatibility
        Preconditions.checkArgument("SRG".equalsIgnoreCase(mapping));
        this.mapping = mapping;
    }

    public int getStackDepth() {
        return stackDepth;
    }

    public void setStackDepth(int stackDepth) {
        this.stackDepth = stackDepth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TargetClassInfo that = (TargetClassInfo) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(mapping, that.mapping)
                .append(stackDepth, that.stackDepth)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(mapping)
                .append(stackDepth)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("mapping", mapping)
                .append("stackDepth", stackDepth)
                .toString();
    }
}
