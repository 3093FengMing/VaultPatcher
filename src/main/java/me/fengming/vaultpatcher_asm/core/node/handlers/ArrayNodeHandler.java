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

    // get the array reference producer (two meaningful steps back: index load then arrayref load)
    private AbstractInsnNode findArrayProducer(AbstractInsnNode aaloadNode) {
        AbstractInsnNode prev = previousMeaningful(aaloadNode); // index load (ILOAD, LDC, ICONST, etc.)
        if (prev == null) return null;
        AbstractInsnNode prev2 = previousMeaningful(prev); // this should be the arrayref provider
        return prev2;
    }

    @Override
    public InsnNode modifyNode() {
        if (this.node.getOpcode() != Opcodes.AALOAD) return this.node;

        // ordinal check as other handlers do
        if (!MatchUtils.matchOrdinal(params.info, params.ordinal)) return this.node;

        AbstractInsnNode producer = findArrayProducer(this.node);
        String nameToMatch = null;
        boolean treatAsMethod = false; // whether to match as method return name

        if (producer == null) {
            // can't identify producer -> skip
            return this.node;
        }

        // If producer is a local var load (ALOAD)
        if (producer instanceof VarInsnNode) {
            VarInsnNode v = (VarInsnNode) producer;
            if (v.getOpcode() == Opcodes.ALOAD) {
                nameToMatch = params.localVariableMap.getOrDefault(v.var, null);
            }
        } else if (producer instanceof FieldInsnNode) {
            FieldInsnNode f = (FieldInsnNode) producer;
            nameToMatch = f.name; // field name (global)
        } else if (producer instanceof MethodInsnNode) {
            MethodInsnNode m = (MethodInsnNode) producer;
            nameToMatch = m.name; // method that returned the array
            treatAsMethod = true;
        } else {
            // other producers (e.g. NEWARRAY results via array creation) - skip
            nameToMatch = null;
        }

        // If no name found, we can't match against config
        if (nameToMatch == null) return this.node;

        // MatchLocal: the last boolean usually signals "isMethod" in this codebase for name matching
        if (MatchUtils.matchLocal(params.info, nameToMatch, treatAsMethod)) {
            // Insert replacement call right after AALOAD (same as other handlers)
            insertReplace(params.classNode.name, params.methodNode, this.node, false);

            // debug info
            String detail = buildDetail(this.node, params.methodNode);
            debugInfo(params.ordinal, "ASMTransformMethod-InsertArray", "[key]", "[value]", detail);
        }

        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {
        info.desc = "AALOAD";
    }
}


