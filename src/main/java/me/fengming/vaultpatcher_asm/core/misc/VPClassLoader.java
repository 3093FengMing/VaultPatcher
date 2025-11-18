package me.fengming.vaultpatcher_asm.core.misc;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class VPClassLoader {
    // misc
    private static final MethodHandles.Lookup PUBLIC_LOOKUP = MethodHandles.publicLookup();

    public static void newClass(ClassLoader parent, String className, byte[] classBytes) {
        String dClassName = StringUtils.dotPackage(className);
        try {
            Method Method_defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            Method_defineClass.setAccessible(true);


            Class<?> loadedClass = (Class<?>) Method_defineClass.invoke(parent, dClassName, classBytes, 0, classBytes.length);
            loadedClass.newInstance();
        } catch (Exception e) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Failed to load class: {}, will try to use unsafe method", dClassName, e);
            UnsafeVPClassLoader.newClass(parent, dClassName, classBytes);
        }
    }

    private static final @Nullable MethodHandle JAVA9_DEFINE_CLASS;
    static {
        MethodHandle mh = null;
        try {
            mh = PUBLIC_LOOKUP.findVirtual(MethodHandles.Lookup.class, "defineClass", MethodType.methodType(Class.class, byte[].class));
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException e) {
            VaultPatcher.LOGGER.warn("[VaultPatcher] Reported illegal access while accessing MethodHandles$Lookup.defineClass()");
        }
        JAVA9_DEFINE_CLASS = mh;
    }

    public static void newClass(MethodHandles.Lookup lookup, String className, byte[] classBytes) {
        if (JAVA9_DEFINE_CLASS != null) {
            try {
                Class<?> ignore = (Class<?>) JAVA9_DEFINE_CLASS.invokeExact(lookup, classBytes);
                return;
            } catch (Throwable e) {
                VaultPatcher.LOGGER.warn("[VaultPatcher] Failed to define class {}. Fallback to Java8 unsafe utilities", className);
            }
        }
        newClass(lookup.lookupClass().getClassLoader(), className, classBytes);
    }
}
