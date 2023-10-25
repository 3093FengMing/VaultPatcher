package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.ASMUtils;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodNodeHandler extends NodeHandler<MethodInsnNode> {
    public MethodNodeHandler(MethodInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public MethodInsnNode modifyNode(boolean disableLocal) {
        if (this.node.desc.endsWith(")Ljava/lang/String;")
                && !this.node.name.equals("__vp_replace")
                && ASMUtils.matchLocal(this.params.info, this.node.name, true)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node);
            debugInfo("ASMTransformMethod-InsertMethodReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
