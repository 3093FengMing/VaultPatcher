package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcNodeHandler extends NodeHandler<LdcInsnNode> {

    public LdcNodeHandler(LdcInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public LdcInsnNode modifyNode() {
        if (this.node.cst instanceof String) {
            String s = (String) this.node.cst;
            String v = Utils.matchPairs(this.params.info.getPairs(), s, false);
            debugInfo("ASMTransformMethod-Ldc", s, v);
            this.node.cst = v;
        }
        return this.node;
    }
}
