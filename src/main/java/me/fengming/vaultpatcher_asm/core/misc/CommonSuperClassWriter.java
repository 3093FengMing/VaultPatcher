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
        return "java/lang/Object";
    }
}
