package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.Arrays;

public class LdcNodeHandler extends NodeHandler<LdcInsnNode> {

    public LdcNodeHandler(LdcInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public LdcInsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] LdcNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Cst: " + this.node.cst);
        if (this.node.cst instanceof String
                && (this.params.ordinal == this.params.info.getTargetClassInfo().getOrdinal() || this.params.info.getTargetClassInfo().getOrdinal() == -1)) {
            String s = (String) this.node.cst;
            String v = Utils.matchPairs(this.params.info.getPairs(), s, false);
            debugInfo("ASMTransformMethod-Ldc", s, v);
            this.node.cst = v;
        }
        return this.node;
    }
}
