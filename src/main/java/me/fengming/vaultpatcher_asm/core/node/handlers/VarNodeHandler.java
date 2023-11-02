package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class VarNodeHandler extends NodeHandler<VarInsnNode> {

    public VarNodeHandler(VarInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public VarInsnNode modifyNode() {
        if ((this.node.getOpcode() == Opcodes.ASTORE || this.node.getOpcode() == Opcodes.ALOAD)
                && ASMUtils.matchLocal(this.params.info, this.params.localVariableMap.getOrDefault(this.node.var, null), false)) {
            ASMUtils.insertReplace(this.params.classNode.name, this.params.methodNode, this.node);
            debugInfo("ASMTransformMethod-InsertLocalVariableStore/Load", "Runtime Determination", "Runtime Determination");
        }
//        // Parameters
//        method.parameters.forEach(p -> {
//            if (p.name.equals(localVariableMap.getOrDefault(varInsnNode.var, null))) {
//                insertReplace(info, method, varInsnNode);
//                Utils.printDebugInfo("Runtime Determination", "ASMTransformMethod-InsertLocalVariableLoad", "Runtime Determination", input.name, info);
//            }
//        });
        return this.node;
    }
}