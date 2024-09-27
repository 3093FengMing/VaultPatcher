package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodNodeHandler extends NodeHandler<MethodInsnNode> {
    public MethodNodeHandler(MethodInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public MethodInsnNode modifyNode() {
        if (!params.disableLocal
                && Utils.matchOrdinal(params.info, params.ordinal)
                && Utils.matchLocal(params.info, this.node.name, true)) {
            Utils.insertReplace(params.classNode.name, params.methodNode, this.node, this.node.desc.endsWith(")Ljava/lang/String"));
            debugInfo(params.ordinal, "ASMTransformMethod-InsertCalledMethodReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {
        info.name = node.name;
        info.desc = node.desc;
    }
}
