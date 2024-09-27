package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldNodeHandler extends NodeHandler<FieldInsnNode> {
    public FieldNodeHandler(FieldInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public FieldInsnNode modifyNode() {
        if (!params.disableLocal
                && (this.node.getOpcode() == Opcodes.GETFIELD || this.node.getOpcode() == Opcodes.PUTFIELD)
                && Utils.matchOrdinal(params.info, params.ordinal)
                && Utils.matchLocal(params.info, this.node.name, false)) {
            Utils.insertReplace(params.classNode.name, params.methodNode, this.node, this.node.desc.equals("Ljava/lang/String;"));
            debugInfo(params.ordinal, "ASMTransformMethod-InsertGlobalVariablePut/Get", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {
        info.name = node.name;
        info.desc = node.desc;
    }
}
