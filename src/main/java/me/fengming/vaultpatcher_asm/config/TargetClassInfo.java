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
    private int ordinal = -1;
    private MatchMode matchMode = MatchMode.FULL;
    private LocalMode localMode = LocalMode.NONE;

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "n":
                case "name": {
                    setName(reader.nextString());
                    break;
                }
                case "m":
                case "method": {
                    setMethod(reader.nextString());
                    break;
                }
                case "l":
                case "local": {
                    setLocal(reader.nextString());
                    break;
                }
                case "o":
                case "ordinal": {
                    setOrdinal(reader.nextInt());
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
        writer.name("name").value("com.example.mod.SomethingClass");
        writer.name("method").value("doSomething");
        writer.name("local").value("Lsomething");
        writer.name("ordinal").value(209);
        writer.endObject();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Utils.isBlank(name)) return;
        char first = name.charAt(0);
        if (first == '@') {
            matchMode = MatchMode.STARTS;
            this.name = name.substring(1);
        } else if (first == '#') {
            matchMode = MatchMode.ENDS;
            this.name = name.substring(1);
        } else {
            matchMode = MatchMode.FULL;
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
            char first = local.charAt(0);
            this.local = local.substring(1);
            if (first == 'V') {
                localMode = LocalMode.LOCAL_VARIABLE;
            } else if (first == 'M') {
                localMode = LocalMode.METHOD_RETURN;
            } else if (first == 'R') {
                localMode = LocalMode.CALL_RETURN;
            } else if (first == 'G' || first == 'F') {
                localMode = LocalMode.GLOBAL_VARIABLE;
            } else {
                localMode = LocalMode.NONE;
            }
        }
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public LocalMode getLocalMode() {
        return localMode;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String toString() {
        return "TargetClassInfo{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", local='" + local + '\'' +
                ", ordinal=" + ordinal +
                ", matchMode=" + matchMode +
                ", localMode=" + localMode +
                '}';
    }

    public enum LocalMode { CALL_RETURN, LOCAL_VARIABLE, METHOD_RETURN, GLOBAL_VARIABLE, NONE }

    public enum MatchMode { FULL, STARTS, ENDS }
}
