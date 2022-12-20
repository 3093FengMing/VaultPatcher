package me.fengming.vaultpatcher.config;

public class targetClass {
    public String name = "";
    public String mapping = "SRG";
    public int stack_depth = 0;

    @Override
    public String toString() {
        return "targetClass{" +
                "name='" + name + '\'' +
                ", mapping='" + mapping + '\'' +
                ", stack_depth=" + stack_depth +
                '}';
    }
}
