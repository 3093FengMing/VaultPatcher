package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldNodeHandler extends NodeHandler<FieldInsnNode> {
    public FieldNodeHandler(FieldInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public FieldInsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] FieldNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Name: " + this.node.name);
        VaultPatcher.debugInfo("[VaultPatcher] Node Desc: " + this.node.desc);
        VaultPatcher.debugInfo("[VaultPatcher] Node Opcode: " + this.node.getOpcode());
        if (!this.params.disableLocal
                && (this.node.getOpcode() == Opcodes.GETFIELD || this.node.getOpcode() == Opcodes.PUTFIELD)
                && ASMUtils.matchOrdinal(this.params.info, this.params.ordinal)
                && ASMUtils.matchLocal(this.params.info, this.node.name, false)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node, this.node.desc.equals("Ljava/lang/String;"));
            debugInfo(this.params.ordinal, "ASMTransformMethod-InsertGlobalVariablePut/Get", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
