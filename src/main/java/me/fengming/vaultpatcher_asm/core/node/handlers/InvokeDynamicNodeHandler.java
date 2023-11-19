package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

import java.util.Arrays;

// jdk8+
public class InvokeDynamicNodeHandler extends NodeHandler<InvokeDynamicInsnNode> {
    public InvokeDynamicNodeHandler(InvokeDynamicInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public InvokeDynamicInsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] InvokeDynamicNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Name: " + this.node.name);
        VaultPatcher.debugInfo("[VaultPatcher] Node BsmArgs: " + Arrays.toString(this.node.bsmArgs));
        if (this.node.name.equals("makeConcatWithConstants")) {
            Object[] bsmArgs = this.node.bsmArgs;
            for (int i = 0; i < bsmArgs.length; i++) {
                if (bsmArgs[i] instanceof String) {
                    String str = (String) bsmArgs[i];
                    String[] parts = str.split("\u0001", -1);
                    for (int j = 0; j < parts.length; j++) {
                        parts[j] = Utils.matchPairs(this.params.info.getPairs(), parts[j], false);
                    }
                    String v = String.join("\u0001", parts);
                    debugInfo("ASMTransformMethod-StringConcat", str.replace("\u0001", "<p>"), v.replace("\u0001", "<p>"));
                    bsmArgs[i] = v;
                }
            }
            this.node.bsmArgs = bsmArgs;
        }
        return this.node;
    }
}
