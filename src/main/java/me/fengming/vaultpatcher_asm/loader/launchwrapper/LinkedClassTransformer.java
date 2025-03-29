package me.fengming.vaultpatcher_asm.loader.launchwrapper;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.transformers.VPClassTransformer;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

public class LinkedClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        ClassNode input = new ClassNode();
        ClassReader cr = new ClassReader(basicClass);
        cr.accept(input, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        boolean patched = false;
        ClassNode patchedClass;
        if (VaultPatcherConfig.isEnableClassPatch()) {
            patchedClass = ClassPatcher.patch(input.name);
            patched = patchedClass != null;
            if (patched) input = patchedClass;
            VaultPatcher.debugInfo("[VaultPatcher] Using Patch: {} {}", input.name, patched);
        }

        Set<TranslationInfo> set = VPLaunchTweaker.classFinding.getOrDefault(name, null);
        if (set == null) return patched ? Utils.nodeToBytes(input) : basicClass;

        for (TranslationInfo info : set) {
            new VPClassTransformer(info).accept(input);
        }
        VaultPatcher.debugInfo("sususu");
        return Utils.nodeToBytes(input);
    }
}
