package me.fengming.vaultpatcher_asm.core.misc;

import me.fengming.vaultpatcher_asm.VaultPatcher;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

final class UnsafeVPClassLoader {
    private static final MethodHandles.Lookup lookup;

    static {
        try {
            // Java 24??
            MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();

            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object theUnsafe = publicLookup.unreflectGetter(unsafeField).invoke();

            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");

            lookup = (MethodHandles.Lookup) getField(publicLookup, unsafeClass).invoke(theUnsafe, implLookupField);
        } catch (Throwable e) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Failed to initialize VPClassLoader: ", e);
            throw new RuntimeException(e);
        }
    }

    // (UF)L
    private static MethodHandle getField(MethodHandles.Lookup lookup, Class<?> unsafeClass) throws Throwable {
        // Unsafe.staticFieldBase(Field) -> Object
        // Unsafe.staticFieldOffset(Field) -> long
        // Unsafe.getObject(Object, long) -> Object

        // (UOJ)L
        MethodHandle m = lookup.findVirtual(unsafeClass, "getObject", MethodType.methodType(Object.class, Object.class, long.class));

        // (UOUF)L
        m = MethodHandles.collectArguments(
                m, 2,
                // (UF)J
                lookup.findVirtual(unsafeClass, "staticFieldOffset", MethodType.methodType(long.class, Field.class))
        );
        // (UUFUF)L
        m = MethodHandles.collectArguments(
                m, 1,
                // (UF)O
                lookup.findVirtual(unsafeClass, "staticFieldBase", MethodType.methodType(Object.class, Field.class))
        );
        // (UF)L
        return MethodHandles.permuteArguments(m, MethodType.methodType(Object.class, unsafeClass, Field.class), 0, 0, 1, 0, 1);
    }

    // misc
    public static void newClass(ClassLoader parent, String className, byte[] classBytes) {
        try {
            MethodType methodType = MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class);
            MethodHandle handle = lookup.findVirtual(ClassLoader.class, "defineClass", methodType);
            handle.invoke(parent, className, classBytes, 0, classBytes.length);
        } catch (Throwable e) {
            VaultPatcher.LOGGER.error("[VaultPatcher] Failed to define class: ", e);
            throw new RuntimeException(e);
        }
    }
}
