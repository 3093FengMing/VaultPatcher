package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodNodeHandler extends NodeHandler<MethodInsnNode> {
    public MethodNodeHandler(MethodInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public MethodInsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] MethodNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Name: " + this.node.name);
        VaultPatcher.debugInfo("[VaultPatcher] Node Desc: " + this.node.desc);
        if (!this.params.disableLocal
                && ASMUtils.matchOrdinal(this.params.info, this.params.ordinal)
                && ASMUtils.matchLocal(this.params.info, this.node.name, true)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node, this.node.desc.endsWith(")Ljava/lang/String"));
            debugInfo(this.params.ordinal, "ASMTransformMethod-InsertCalledMethodReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
