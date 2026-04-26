package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

import java.io.IOException;
import java.util.*;

public class TargetClassInfo {
    private String dynamicName = ""; // used for dynamic replace
    private String method = "";
    private String local = "";
    private String annotation = "";
    private String annoKey = "";
    private String annoType = "ALL";
    private List<Pair<Integer, Integer>> ordinal = new ArrayList<>();{ordinal.add(new Pair<>(-1, -1));}
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
                case "annotation": {
                    readAnnotation(reader);
                    break;
                }
                case "o":
                case "ordinal": {
                    JsonToken peeked = reader.peek();
                    if (peeked == JsonToken.NUMBER) {
                        this.ordinal.clear();
                        Pair<Integer, Integer> ordinalPair = new Pair<>(-1, -1);
                        ordinalPair.first=ordinalPair.second=reader.nextInt();
                        this.ordinal.add(ordinalPair);
                    } else if (peeked == JsonToken.STRING) {
                        this.ordinal.clear();
                        String[] ordinals = reader.nextString().split(",");
                        for (String ordinal : ordinals) {
                            Pair<Integer, Integer> ordinalPair = new Pair<>(-1, -1);
                            if (ordinal.contains("-")) {
                                String[] upAndDown = ordinal.split("-", 2);
                                ordinalPair.first = Integer.parseInt(upAndDown[0]);
                                ordinalPair.second = upAndDown[1].equalsIgnoreCase("e") ? -1 : Integer.parseInt(upAndDown[1]);
                                this.ordinal.add(ordinalPair);
                            } else {
                                ordinalPair.first=ordinalPair.second=Integer.parseInt(ordinal.trim());
                                this.ordinal.add(ordinalPair);
                            }
                        }

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

    private void readAnnotation(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "desc":
                case "descriptor": {
                    setAnnotation(reader.nextString());
                    break;
                }
                case "ak":
                case "annokey": {
                    setAnnotationKey(reader.nextString());
                    break;
                }
                case "type": {
                    setAnnotationType(reader.nextString());
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

    public void setAnnotation(String annotation) {
        this.annotation = "L" + annotation + ";";
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotationKey(String annoKey) {
        this.annoKey = annoKey;
    }

    public String getAnnoKey() {
        return annoKey;
    }

    public void setAnnotationType(String annoType) {
        switch (annoType) {
            case "C":
            case "CLASS": {
                this.annoType = "CLASS";
                break;
            }
            case "F":
            case "FIELD": {
                this.annoType = "FIELD";
                break;
            }
            case "M":
            case "METHOD": {
                this.annoType = "METHOD";
                break;
            }
            default: {
                this.annoType = "ALL";
                break;
            }

        }
    }

    public String getAnnoType() {
        return annoType;
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


    public List<Pair<Integer, Integer>> getOrdinal() {
        return this.ordinal;
    }

    public enum LocalMode {INVOKE_RETURN, LOCAL_VARIABLE, METHOD_RETURN, GLOBAL_VARIABLE, ARRAY_ELEMENT, NONE}

    public enum MatchMode {FULL, STARTS, ENDS}
}
