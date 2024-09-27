package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class VarNodeHandler extends NodeHandler<VarInsnNode> {

    public VarNodeHandler(VarInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public VarInsnNode modifyNode() {
        if (!params.disableLocal
                && (this.node.getOpcode() == Opcodes.ASTORE || this.node.getOpcode() == Opcodes.ALOAD)
                && Utils.matchOrdinal(params.info, params.ordinal)
                && Utils.matchLocal(params.info, params.localVariableMap.getOrDefault(this.node.var, null), false)) {
            Utils.insertReplace(params.classNode.name, params.methodNode, this.node, false);
            debugInfo(params.ordinal, "ASMTransformMethod-InsertLocalVariableStore/Load", "Runtime Determination", "Runtime Determination");
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

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {
        info.var = node.var;
        info.varString = params.localVariableMap.getOrDefault(this.node.var, null);
    }
}
