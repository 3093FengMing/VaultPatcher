package me.fengming.vaultpatcher_asm.core.misc;

import org.objectweb.asm.ClassWriter;

/**
 * @author FengMing
 */
public class CommonSuperClassWriter extends ClassWriter {
    public CommonSuperClassWriter() {
        super(ClassWriter.COMPUTE_FRAMES);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        if (type1.equals(type2)) return type1;
        if (type1.startsWith("java/") && type2.startsWith("java/")) {
            // jdk platform types
            return super.getCommonSuperClass(type1, type2);
        }
        // Prevent loading
        return "java/lang/Object";
    }
}
