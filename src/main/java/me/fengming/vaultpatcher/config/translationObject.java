package me.fengming.vaultpatcher.config;

public class translationObject {
    public targetClass target_class = null;
    public String key = "";
    public String value = "";

    @Override
    public String toString() {
        return "translationObject{" +
                "target_class=" + target_class.toString() +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
