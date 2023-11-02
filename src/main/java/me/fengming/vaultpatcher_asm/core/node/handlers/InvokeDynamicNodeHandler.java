package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

// jdk8+
public class InvokeDynamicNodeHandler extends NodeHandler<InvokeDynamicInsnNode> {
    public InvokeDynamicNodeHandler(InvokeDynamicInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public InvokeDynamicInsnNode modifyNode() {
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