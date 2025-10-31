package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

import java.io.IOException;

public class TargetClassInfo {
    private String dynamicName = ""; // used for dynamic replace
    private String method = "";
    private String local = "";
    private Pair<Integer, Integer> ordinal = new Pair<>(-1, -1);
    private MatchMode matchMode = MatchMode.FULL;
    private LocalMode localMode = LocalMode.NONE;

    public void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
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
                    JsonToken peeked = reader.peek();
                    if (peeked == JsonToken.NUMBER) {
                        this.ordinal.first = this.ordinal.second = reader.nextInt();
                    } else if (peeked == JsonToken.STRING) {
                        String ordinal = reader.nextString();
                        String[] upAndDown = ordinal.split("-", 2);
                        this.ordinal.first = Integer.parseInt(upAndDown[0]);
                        this.ordinal.second = upAndDown[1].equalsIgnoreCase("e") ? -1 : Integer.parseInt(upAndDown[1]);
                    } else {
                        VaultPatcher.LOGGER.warn("Couldn't read ordinal: {} as {}", reader.nextString(), peeked);
                    }
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

    public String getDynamicName() {
        return dynamicName;
    }

    public void setDynamicName(String dynamicName) {
        if (StringUtils.isBlank(dynamicName)) return;
        char first = dynamicName.charAt(0);
        switch (first) {
            case '@': {
                matchMode = MatchMode.STARTS;
                this.dynamicName = dynamicName.substring(1);
                break;
            }
            case '#': {
                matchMode = MatchMode.ENDS;
                this.dynamicName = dynamicName.substring(1);
                break;
            }
            default: {
                matchMode = MatchMode.FULL;
                this.dynamicName = dynamicName;
                break;
            }
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
        if (StringUtils.isBlank(local)) {
            this.local = local;
        } else {
            char first = local.charAt(0);
            this.local = local.substring(1);
            switch (first) {
                case 'V': {
                    localMode = LocalMode.LOCAL_VARIABLE;
                    break;
                }
                case 'M': {
                    localMode = LocalMode.METHOD_RETURN;
                    break;
                }
                case 'R': {
                    localMode = LocalMode.INVOKE_RETURN;
                    break;
                }
                case 'G':
                case 'F': {
                    localMode = LocalMode.GLOBAL_VARIABLE;
                    break;
                }
                case 'A': {
                    localMode = LocalMode.ARRAY_ELEMENT;
                    break;
                }
                default: {
                    localMode = LocalMode.NONE;
                    break;
                }
            }
        }
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public LocalMode getLocalMode() {
        return localMode;
    }


    public Pair<Integer, Integer> getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(Pair<Integer, Integer> ordinal) {
        this.ordinal = ordinal;
    }

    public enum LocalMode {INVOKE_RETURN, LOCAL_VARIABLE, METHOD_RETURN, GLOBAL_VARIABLE, ARRAY_ELEMENT, NONE}

    public enum MatchMode {FULL, STARTS, ENDS}
}
