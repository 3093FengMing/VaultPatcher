package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.*;

public abstract class NodeHandler<E extends AbstractInsnNode> {
    protected final E node;
    protected final NodeHandlerParameters params;

    public NodeHandler(E node, NodeHandlerParameters params) {
        this.node = node;
        this.params = params;
    }

    public abstract E modifyNode();

    public void debugInfo(String method, String source, String ret) {
        Utils.printDebugInfo(source, method, ret, this.params.classNode.name, this.params.info);
    }

    public void debugInfo(int ordinal, String method, String source, String ret) {
        Utils.printDebugInfo(ordinal, source, method, ret, this.params.classNode.name, this.params.info);
    }

    public static NodeHandler<? extends AbstractInsnNode> getHandlerByNode(AbstractInsnNode node, NodeHandlerParameters params) {
        switch (node.getType()) {
            case 9:
                return new LdcNodeHandler((LdcInsnNode) node, params);
            case 6:
                return new InvokeDynamicNodeHandler((InvokeDynamicInsnNode) node, params);
            case 5:
                return new MethodNodeHandler((MethodInsnNode) node, params);
            case 2:
                return new VarNodeHandler((VarInsnNode) node, params);
            case 0:
                return new InsnNodeHandler((InsnNode) node, params);
        }
        return null;
    }
}
