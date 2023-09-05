package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.Pairs;

import java.util.HashMap;

public class ASMUtils {

    public static Pairs __pairsByArrays(String[] k, String[] v) {
        if (k.length != v.length) return null;
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < k.length; i++) {
            map.put(k[i], v[i]);
        }
        return new Pairs(map);
    }

    public static String __replaceMethod(String key, Pairs p) {
        return Utils.matchPairs(p, key);
    }

//    public static String __getVariableByNode(VarInsnNode node) {
//        return "";
//    }
}
