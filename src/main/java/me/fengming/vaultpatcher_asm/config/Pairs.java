package me.fengming.vaultpatcher_asm.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pairs {
    private HashMap<String, String> pairsMap = null;
    private String lastKey = null;
    private String lastValue = null;

    // optimization (only dynamic)
    private List<Pair<String, String>> pairsList = null;
    private boolean nonFullMatch = false;
    private final boolean dyn;

    public Pairs() { // not use dynamic mode
        this(false);
    }

    public Pairs(boolean dyn) {
        this.dyn = dyn;
        if (dyn) {
            this.pairsList = new ArrayList<>();
        } else {
            this.pairsMap = new HashMap<>();
        }
    }

    public void readJson(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.peek() != JsonToken.END_ARRAY) {
            reader.beginObject();
            while (reader.peek() != JsonToken.END_OBJECT) {
                switch (reader.nextName()) {
                    case "k":
                    case "key": {
                        setKey(reader.nextString());
                        break;
                    }
                    case "v":
                    case "value": {
                        setValue(reader.nextString());
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
        reader.endArray();
    }


    public HashMap<String, String> getMap() {
        return pairsMap;
    }
    public List<Pair<String, String>> getList() {
        return pairsList;
    }

    public void setKey(String key) {
        if (lastValue == null) {
            // wait value reading
            lastKey = key;
            return;
        }
        if (dyn) {
            nonFullMatch |= lastValue.length() > 0 && lastValue.charAt(0) == '@'; // must be OR
            pairsList.add(new Pair<>(key, lastValue));
        } else {
            pairsMap.put(key, lastValue);
        }
        lastValue = null;
        lastKey = null;
    }

    public void setValue(String value) {
        if (lastKey == null) {
            // wait key reading
            lastValue = value;
            return;
        }
        if (dyn) {
            nonFullMatch |= value.length() > 0 && value.charAt(0) == '@'; // must be OR
            pairsList.add(new Pair<>(lastKey, value));
        } else {
            pairsMap.put(lastKey, value);
        }
        lastValue = null;
        lastKey = null;
    }

    public String getValue(String key) {
        if (dyn) {
            if (nonFullMatch) return null; // Always ignore this value in Utils class
            Pair<String, String> pair = pairsList.stream()
                    .filter(e -> e.first.equals(key))
                    .findFirst()
                    .orElse(null); // Perhaps for is faster than StreamAPI?
            return pair == null ? key : pair.second;
        } else {
            return pairsMap.getOrDefault(key, key);
        }
    }

    public boolean isNonFullMatch() {
        return nonFullMatch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        if (dyn) {
            pairsList.forEach(e -> sb.append("'").append(e.first).append("'='").append(e.second).append("'").append(","));
        } else {
            pairsMap.forEach((key, value) -> sb.append("'").append(key).append("'='").append(value).append("'").append(","));
        }
        sb.deleteCharAt(sb.length() - 1).append("}");
        return sb.toString();
    }
}
