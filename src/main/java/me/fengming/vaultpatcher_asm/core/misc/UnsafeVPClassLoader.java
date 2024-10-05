package me.fengming.vaultpatcher_asm.core.misc;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public class UnsafeVPClassLoader {
    public static Unsafe unsafe;
    public static MethodHandles.Lookup lookup;

    static {
        try {
            Field Field_theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            Field_theUnsafe.setAccessible(true);
            unsafe = (Unsafe) Field_theUnsafe.get(null);
            Field Field_IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(Field_IMPL_LOOKUP), unsafe.staticFieldOffset(Field_IMPL_LOOKUP));
        } catch (Throwable e) {
            VaultPatcher.LOGGER.error("Failed to initialize VPClassLoader: ", e);
            throw new RuntimeException(e);
        }
    }

    // misc
    public static void newClass(ClassLoader parent, String className, byte[] classBytes) {
        try {
            MethodType methodType = MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class);
            MethodHandle handle = lookup.findVirtual(ClassLoader.class, "defineClass", methodType);
            handle.invoke(parent, className, classBytes, 0, classBytes.length);
        } catch (Throwable e) {
            VaultPatcher.LOGGER.error("Failed to define class: ", e);
            throw new RuntimeException(e);
        }
    }
}
