package me.fengming.vaultpatcher_asm.core.transformers;

import java.lang.reflect.Method;

public class VPClassLoader {
    // hack
    public static void newClass(String className, byte[] classBytes) {
        try {
            Class clazz = Class.forName("java.lang.ClassLoader");
            Method method = clazz.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            Class modifiedClazz = (Class) method.invoke(Thread.currentThread().getContextClassLoader(), className, classBytes, 0, classBytes.length);
            modifiedClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error Reflecting", e);
        }
    }
}
