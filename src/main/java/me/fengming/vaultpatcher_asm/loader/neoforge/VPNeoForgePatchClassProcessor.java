package me.fengming.vaultpatcher_asm.loader.neoforge;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.patch.ClassPatcher;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import net.neoforged.neoforgespi.transformation.ProcessorName;
import net.neoforged.neoforgespi.transformation.SimpleClassProcessor;
import net.neoforged.neoforgespi.transformation.SimpleTransformationContext;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class VPNeoForgePatchClassProcessor extends SimpleClassProcessor {
    private final Set<String> classNames;

    public VPNeoForgePatchClassProcessor(Set<String> classNames) {
        this.classNames = classNames;
    }

    @Override
    public ProcessorName name() {
        return new ProcessorName("vaultpatcher", "patches");
    }

    @Override
    public void transform(ClassNode input, SimpleTransformationContext context) {
        VaultPatcher.debugInfo("[VaultPatcher] Using Patch: {}", input.name);
        ClassNode patched = ClassPatcher.patch(input);
        if (patched != input) {
            Utils.deepCopyClass(input, patched);
        }
    }

    @Override
    public Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        for (String n : classNames) {
            if (StringUtils.isBlank(n)) continue;
            targets.add(new Target(StringUtils.dotPackage(n)));
        }
        return targets;
    }
}
