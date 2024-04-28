package me.fengming.vaultpatcher_asm.core.node;

import me.fengming.vaultpatcher_asm.core.node.handlers.NodeHandler;

import java.util.Arrays;

public class HandlerDebugInfo {
    private final NodeHandler<?> handler;
    public NodeHandlerParameters parameters;
    public int opcode;
    public Object cst = null;
    public String name = null;
    public String desc = null;
    public int var = -1;
    public String varString = null;
    public Object[] bsmArgs = null;

    public HandlerDebugInfo(NodeHandler<?> handler, NodeHandlerParameters parameters, int opcode) {
        this.handler = handler;
        this.parameters = parameters;
        this.opcode = opcode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(handler.getClass().getName().substring("me.fengming.vaultpatcher_asm.core.node.handlers".length()));
        sb.append("HandlerDebugInfo{").
        append("parameters=").append(parameters).
        append(", opcode='").append(opcode).append('\'');
        if (cst != null) sb.append(", cst='").append(cst).append('\'');
        if (name != null) sb.append(", name='").append(name).append('\'');
        if (desc != null) sb.append(", desc='").append(desc).append('\'');
        if (var != -1) sb.append(", var=").append(var);
        if (varString != null) sb.append(", varString='").append(varString).append('\'');
        if (bsmArgs != null) sb.append(", bsmArgs=").append(Arrays.deepToString(bsmArgs));
        sb.append('}');
        return sb.toString();
    }
}
