package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.AbstractInsnNode;

public abstract class NodeHandler<E extends AbstractInsnNode> {
    protected final E node;
    protected final NodeHandlerParameters params;

    public NodeHandler(E node, NodeHandlerParameters params) {
        this.node = node;
        this.params = params;
    }

    public abstract E modifyNode(boolean disableLocal);

    public void debugInfo(String method, String source, String ret) {
        Utils.printDebugInfo(source, method, ret, this.params.classNode.name, this.params.info);
    }
}
