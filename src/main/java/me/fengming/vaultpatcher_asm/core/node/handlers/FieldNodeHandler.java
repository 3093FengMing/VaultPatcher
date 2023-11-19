package me.fengming.vaultpatcher_asm.core.node.handlers;

import jdk.internal.org.objectweb.asm.Opcodes;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldNodeHandler extends NodeHandler<FieldInsnNode> {
    public FieldNodeHandler(FieldInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public FieldInsnNode modifyNode() {
        if (!this.params.disableLocal
                && (this.node.getOpcode() == Opcodes.GETFIELD || this.node.getOpcode() == Opcodes.PUTFIELD)
                && this.node.desc.equals("Ljava/lang/String;")
                && ASMUtils.matchLocal(this.params.info, this.node.name, false, false)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node);
            debugInfo(this.params.ordinal, "ASMTransformMethod-InsertGlobalVariablePut/Get", "Runtime Determination", "Runtime Determination");
        }
        return this.node;
    }
}
