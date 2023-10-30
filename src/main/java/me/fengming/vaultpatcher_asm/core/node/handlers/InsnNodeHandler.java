package me.fengming.vaultpatcher_asm.core.node.handlers;

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
        if (!this.params.disableLocal && this.params.methodNode.desc.endsWith(")Ljava/lang/String;")
                && this.node.getOpcode() == Opcodes.ARETURN
                && ASMUtils.matchLocal(params.info, params.classNode.name, false)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node);
            debugInfo("ASMTransformMethod-InsertReturn", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
