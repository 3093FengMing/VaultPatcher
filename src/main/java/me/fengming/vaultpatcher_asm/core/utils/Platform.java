package me.fengming.vaultpatcher_asm.core.utils;

/**
 * @author FengMing
 */

public enum Platform {
    UNDEFINED,
    Fabric, Forge1_6, Forge1_13,
    Neo21_9,
    JAVA_8, JAVA_11, JAVA_17, JAVA_21;

    @Deprecated
    public static Platform getJavaVersion(String s) {
        switch (s) {
            case "1.8":
                return JAVA_8;
            case "11":
                return JAVA_11;
            case "17":
                return JAVA_17;
            case "21":
                return JAVA_21;
            default:
                return UNDEFINED;
        }
    }
}
