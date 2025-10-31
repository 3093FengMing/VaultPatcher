package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcNodeHandler extends NodeHandler<LdcInsnNode> {

    public LdcNodeHandler(LdcInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public LdcInsnNode modifyNode() {
        if (this.node.cst instanceof String
                && MatchUtils.matchOrdinal(params.info, params.ordinal)
                && (params.info.getTargetClassInfo().getLocal() == null || params.info.getTargetClassInfo().getLocal().isEmpty())) {
            String s = (String) this.node.cst;
            String v = MatchUtils.matchPairs(params.info.getPairs(), s, false);
            String detail = buildDetail(this.node, params.methodNode);
            debugInfo(params.ordinal, "ASMTransformMethod-Ldc", s, v, detail);
            this.node.cst = v;
        }
        return this.node;
    }

    @Override
    public void addDebugInfo(HandlerDebugInfo info) {
        info.cst = node.cst;
    }
}
