package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class VarNodeHandler extends NodeHandler<VarInsnNode> {

    public VarNodeHandler(VarInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public VarInsnNode modifyNode() {
        if (!params.disableLocal
                && (this.node.getOpcode() == Opcodes.ALOAD)
                && MatchUtils.matchOrdinal(params.info, params.ordinal)
                && (MatchUtils.matchLocal(params.info, params.localVariableMap.getOrDefault(this.node.var, null), false) || MatchUtils.matchLocalIndex(params.info, this.node.var))) {
            insertReplace(params.classNode.name, params.methodNode, this.node, false);
            String detail = buildDetail(this.node, params.methodNode, params.info.getPairs());
            debugInfo(params.ordinal, "ASMTransformMethod-InsertLocalVariableStore/Load", "[key]", "[value]", detail);
        }
        return this.node;
    }
}
