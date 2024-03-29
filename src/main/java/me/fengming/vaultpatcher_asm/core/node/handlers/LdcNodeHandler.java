package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.ASMUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcNodeHandler extends NodeHandler<LdcInsnNode> {

    public LdcNodeHandler(LdcInsnNode node, NodeHandlerParameters params) {
        super(node, params);
    }

    @Override
    public LdcInsnNode modifyNode() {
        VaultPatcher.debugInfo("[VaultPatcher] LdcNodeHandler");
        VaultPatcher.debugInfo("[VaultPatcher] Params: " + this.params.toString());
        VaultPatcher.debugInfo("[VaultPatcher] Node Cst: " + this.node.cst);
        if (this.node.cst instanceof String && ASMUtils.matchOrdinal(this.params.info, this.params.ordinal)) {
            String s = (String) this.node.cst;
            String v = Utils.matchPairs(this.params.info.getPairs(), s, false);
            debugInfo("ASMTransformMethod-Ldc", s, v);
            this.node.cst = v;
        }
        return this.node;
    }
}
