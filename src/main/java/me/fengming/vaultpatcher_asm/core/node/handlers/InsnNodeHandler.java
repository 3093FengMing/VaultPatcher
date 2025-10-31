package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
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
                && MatchUtils.matchOrdinal(params.info, params.ordinal)
                && MatchUtils.matchLocal(params.info, params.methodNode.name, true)) {
            insertReplace(params.classNode.name, params.methodNode, this.node, false);
            String detail = buildDetail(this.node, params.methodNode);
            debugInfo(params.ordinal, "ASMTransformMethod-InsertMethodReturn", "[key]", "[value]", detail);
        }
        return this.node;
    }
}
