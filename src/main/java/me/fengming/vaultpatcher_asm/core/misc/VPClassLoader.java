package me.fengming.vaultpatcher_asm.core.misc;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;

import java.lang.reflect.Method;

public class VPClassLoader {
    // misc
    public static void newClass(ClassLoader parent, String className, byte[] classBytes) {
        String dClassName = StringUtils.dotPackage(className);
        try {
            Class<?> Instance_ClassLoader = Class.forName("java.lang.ClassLoader");
            Method Method_defineClass = Instance_ClassLoader.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            Method_defineClass.setAccessible(true);
            Class<?> loadedClass = (Class<?>) Method_defineClass.invoke(parent, dClassName, classBytes, 0, classBytes.length);
            loadedClass.newInstance();
        } catch (Exception e) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Failed to load class: {}, will try to use unsafe method", dClassName, e);
            UnsafeVPClassLoader.newClass(parent, dClassName, classBytes);
        }
    }
}
