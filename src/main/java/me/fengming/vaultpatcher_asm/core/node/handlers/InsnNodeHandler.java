package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
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
        if (!params.disableLocal
                && this.node.getOpcode() == Opcodes.ARETURN
                && ASMUtils.matchOrdinal(params.info, params.ordinal)
                && ASMUtils.matchLocal(params.info, params.methodNode.name, true)) {
            ASMUtils.insertReplace(params.classNode.name, params.methodNode, this.node, false);
            debugInfo(params.ordinal, "ASMTransformMethod-InsertMethodReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {}
}
