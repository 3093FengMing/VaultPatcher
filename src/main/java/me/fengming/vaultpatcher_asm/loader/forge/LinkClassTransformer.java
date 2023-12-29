package me.fengming.vaultpatcher_asm.loader.forge;

import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.ClassNode;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;

public class LinkClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode o = new ClassNode();
        ClassReader cr = new ClassReader(basicClass);
        cr.accept(o, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        new VPClassTransformer(VPLaunchTweaker.classFinding.get(name)).accept(o);
        return Utils.nodeToBytes(o);
    }
}
