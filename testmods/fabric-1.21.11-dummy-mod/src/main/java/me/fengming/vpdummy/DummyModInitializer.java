package me.fengming.vpdummy;

import me.fengming.vpdummy.dummy.AnnotationFieldDummy;
import me.fengming.vpdummy.dummy.AnnotationMethodDummy;
import me.fengming.vpdummy.dummy.AnnotationMixedDummy;
import me.fengming.vpdummy.dummy.AnnotationNestedDummy;
import net.fabricmc.api.ModInitializer;

public final class DummyModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Explicitly touch dummy targets so class transformation definitely runs.
        forceLoadTargets();
        probeDynamicStrings();
    }

    private static void forceLoadTargets() {
        try {
            Class.forName("me.fengming.vpdummy.dummy.AnnotationFieldDummy");
            Class.forName("me.fengming.vpdummy.dummy.AnnotationMethodDummy");
            Class.forName("me.fengming.vpdummy.dummy.AnnotationMixedDummy");
            Class.forName("me.fengming.vpdummy.dummy.AnnotationNestedDummy");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load dummy annotation targets", e);
        }

        AnnotationFieldDummy fieldDummy = new AnnotationFieldDummy();
        AnnotationMethodDummy methodDummy = new AnnotationMethodDummy();
        AnnotationMixedDummy mixedDummy = new AnnotationMixedDummy();
        AnnotationNestedDummy nestedDummy = new AnnotationNestedDummy();

        fieldDummy.getJoined();
        methodDummy.method01("A", "B");
        methodDummy.method02("C");
        methodDummy.method03();
        mixedDummy.combine("P", "S");
        mixedDummy.ordinalProbe();
        nestedDummy.nestedMethod();
    }

    private static void probeDynamicStrings() {
        // Feed strings into Minecraft LiteralContents constructor via reflection.
        // VaultPatcher's dynamic hook is injected there on Fabric.
        String[] probes = {
                "FIELD_CONST_RAW_01",
                "LOCAL_RAW_01",
                "MIX_LOCAL_RAW_01",
                "NESTED_DUMMY_METHOD",
                "VP_TEST_DYNAMIC_SAMPLE",
                "SOME_RAW_PAYLOAD"
        };
        try {
            Class<?> literalClass = Class.forName("net.minecraft.class_2585");
            java.lang.reflect.Constructor<?> ctor = literalClass.getDeclaredConstructor(String.class);
            ctor.setAccessible(true);
            for (String s : probes) {
                ctor.newInstance(s);
            }
        } catch (Throwable ignored) {
            // No hard failure if Minecraft internals differ in current runtime.
        }
    }
}
