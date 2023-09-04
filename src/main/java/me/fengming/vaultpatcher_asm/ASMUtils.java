package me.fengming.vaultpatcher_asm;

import me.fengming.vaultpatcher_asm.config.Pairs;
import org.objectweb.asm.tree.VarInsnNode;

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

    public static String __replaceMethod(String key, Pairs p) {
        return Utils.matchPairs(p, key);
    }

    public static String __getVariableByNode(VarInsnNode node) {
        return "";
    }
}
