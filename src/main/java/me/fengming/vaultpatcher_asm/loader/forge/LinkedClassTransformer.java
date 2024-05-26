package me.fengming.vaultpatcher_asm.loader.forge;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class LinkedClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        TranslationInfo info = VPLaunchTweaker.classFinding.getOrDefault(name, null);
        if (info == null) return basicClass;

        ClassNode o = new ClassNode();
        ClassReader cr = new ClassReader(basicClass);
        cr.accept(o, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        new VPClassTransformer(VPLaunchTweaker.classFinding.getOrDefault(name, null)).accept(o);
        return Utils.nodeToBytes(o);
    }
}
