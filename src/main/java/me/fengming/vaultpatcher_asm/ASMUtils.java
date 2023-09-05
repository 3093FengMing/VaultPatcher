package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.Pairs;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ASMUtils {
    public static String __getClassName(Class<?> c) {
        return c.getName().replace(".", "/");
    }

    public static String __makeMethodDesc(Class<?> ret, Class<?>... param) {
        StringBuilder r = new StringBuilder("(");
        for (Class<?> p1 : param) {
            r.append("L").append(__getClassName(p1)).append(";");
        }
        r.append(")L").append(__getClassName(ret)).append(";");
        return r.toString();
    }

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

    public static String __getVariableByNode(VarInsnNode node) {
        return "";
    }
}
