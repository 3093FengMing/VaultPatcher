package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.fengming.vaultpatcher_asm.core.utils.Utils;

import java.io.IOException;

public class TargetClassInfo {
    private String name = "";
    private String method = "";
    private String local = "";
    private byte matchMode = 0;
    private byte localMode = 0;

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "name": {
                    setName(reader.nextString());
                    break;
                }
                case "method": {
                    setMethod(reader.nextString());
                    break;
                }
                case "local": {
                    setLocal(reader.nextString());
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
        writer.name("name").value(getName());
        writer.name("method").value(getMethod());
        writer.name("local").value(getLocal());
        writer.endObject();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        char f = name.charAt(0);
        if (f == '@') {
            matchMode = 1;
            this.name = name.substring(1);
        } else if (f == '#') {
            matchMode = 2;
            this.name = name.substring(1);
        } else {
            matchMode = 0;
            this.name = name;
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        if (Utils.isBlank(local)) {
            this.local = local;
        } else {
            if (local.charAt(0) == 'V') {
                localMode = 1;
            } else if (local.charAt(0) == 'M') {
                localMode = 0;
            } else localMode = 2;
            this.local = local.substring(1);
        }
    }

    public byte getMatchMode() {
        return matchMode;
    }

    public byte getLocalMode() {
        return localMode;
    }

    @Override
    public String toString() {
        return "TargetClassInfo{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", local='" + local + '\'' +
                ", matchMode=" + matchMode +
                ", localMode=" + localMode +
                '}';
    }
}
