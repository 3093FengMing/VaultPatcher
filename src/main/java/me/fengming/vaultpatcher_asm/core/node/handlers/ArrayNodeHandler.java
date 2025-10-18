package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ArrayNodeHandler extends NodeHandler<InsnNode> {

    public ArrayNodeHandler(InsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    // helper: skip non-instruction nodes (labels, frames, line numbers)
    private AbstractInsnNode previousMeaningful(AbstractInsnNode n) {
        AbstractInsnNode p = n.getPrevious();
        while (p != null) {
            int t = p.getType();
            if (t == AbstractInsnNode.LABEL || t == AbstractInsnNode.LINE || t == AbstractInsnNode.FRAME) {
                p = p.getPrevious();
                continue;
            }
            return p;
        }
        return null;
    }

    // get the array reference producer (index load -> arrayref load)
    private AbstractInsnNode findArrayProducer(AbstractInsnNode aaloadNode) {
        AbstractInsnNode prev = previousMeaningful(aaloadNode); // index load (ILOAD, LDC, ICONST, etc.)
        if (prev == null) return null;
        // arrayref provider
        return previousMeaningful(prev);
    }

    @Override
    public InsnNode modifyNode() {
        AbstractInsnNode producer = findArrayProducer(this.node);
        String nameToMatch = null;
        if (this.node.getOpcode() != Opcodes.AALOAD || !MatchUtils.matchOrdinal(params.info, params.ordinal) || producer == null) return this.node;
        // Get name according to the type of producer
        if (producer instanceof VarInsnNode) {
            VarInsnNode v = (VarInsnNode) producer;
            if (v.getOpcode() == Opcodes.ALOAD) {
                nameToMatch = params.localVariableMap.getOrDefault(v.var, null);
            }
        } else if (producer instanceof FieldInsnNode) {
            FieldInsnNode f = (FieldInsnNode) producer;
            nameToMatch = f.name;
        } else if (producer instanceof MethodInsnNode) {
            MethodInsnNode m = (MethodInsnNode) producer;
            nameToMatch = m.name;
        }

        // The 'false' here is just a placeholder for parsing the param
        if (MatchUtils.matchLocal(params.info, nameToMatch, false)) {
            insertReplace(params.classNode.name, params.methodNode, this.node, false);
            String detail = buildDetail(this.node, params.methodNode);
            debugInfo(params.ordinal, "ASMTransformMethod-InsertArray", "[key]", "[value]", detail);
        }
        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {}
}


