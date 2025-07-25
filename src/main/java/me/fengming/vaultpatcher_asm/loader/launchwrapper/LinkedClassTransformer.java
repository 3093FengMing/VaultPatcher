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

import java.util.Set;

@SuppressWarnings("unused")
public class LinkedClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        ClassNode input = new ClassNode();
        ClassReader cr = new ClassReader(basicClass);
        cr.accept(input, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        boolean patched = false;
        if (VaultPatcherConfig.isEnableClassPatch()) {
            ClassNode patchedClass = ClassPatcher.patch(input.name);
            if (patchedClass != null) {
                patched = true;
                input = patchedClass;
                VaultPatcher.debugInfo("[VaultPatcher] Using Patch: {}", input.name);
            }
        }

        Set<TranslationInfo> set = Utils.translationInfoMap.getOrDefault(name, null);
        if (set == null) return patched ? Utils.nodeToBytes(input) : basicClass;

        new VPClassTransformer(set).accept(input);
        return Utils.nodeToBytes(input);
    }
}
