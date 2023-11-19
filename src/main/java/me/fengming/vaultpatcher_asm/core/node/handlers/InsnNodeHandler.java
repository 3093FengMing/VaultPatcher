package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;

public class InsnNodeHandler extends NodeHandler<InsnNode> {
    public InsnNodeHandler(InsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public InsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] InsnNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Opcode: " + this.node.getOpcode());
        if (!this.params.disableLocal
                && this.node.getOpcode() == Opcodes.ARETURN
                && (this.params.ordinal == this.params.info.getTargetClassInfo().getOrdinal() || this.params.info.getTargetClassInfo().getOrdinal() == -1)
                && ASMUtils.matchLocal(this.params.info, this.params.methodNode.name, true)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node);
            debugInfo(this.params.ordinal, "ASMTransformMethod-InsertMethodReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
