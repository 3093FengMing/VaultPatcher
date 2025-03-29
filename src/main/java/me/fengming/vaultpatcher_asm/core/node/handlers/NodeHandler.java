package me.fengming.vaultpatcher_asm.core.node.handlers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.core.node.HandlerDebugInfo;
import me.fengming.vaultpatcher_asm.core.node.NodeHandlerParameters;
import me.fengming.vaultpatcher_asm.core.utils.StringUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public abstract class NodeHandler<E extends AbstractInsnNode> {
    protected final E node;
    protected final NodeHandlerParameters params;

    public NodeHandler(E node, NodeHandlerParameters params) {
        this.node = node;
        this.params = params;
    }

    public static void insertReplace(String className, MethodNode method, AbstractInsnNode nodePosition, boolean isString) {
        method.instructions.insert(nodePosition, new MethodInsnNode(Opcodes.INVOKESTATIC, StringUtils.rawPackage(className), "__vp_replace", isString ? "(Ljava/lang/String;)Ljava/lang/String;" : "(Ljava/lang/Object;)Ljava/lang/String;", false));
    }

    public E _modifyNode() {
        if (Utils.debug.isEnable() && Utils.debug.isOutputNodeDebug()) {
            HandlerDebugInfo debugInfo = new HandlerDebugInfo(this, params, node.getOpcode());
            addDebugInfo(debugInfo);
            VaultPatcher.debugInfo(debugInfo.toString());
        }
        return this.modifyNode();
    }

    public abstract E modifyNode();

    public abstract void addDebugInfo(HandlerDebugInfo info);

    public void debugInfo(int ordinal, String method, String source, String ret) {
        Utils.printDebugInfo(ordinal, source, method, ret, params.classNode.name, params.info);
    }

    public static NodeHandler<? extends AbstractInsnNode> getHandlerByNode(AbstractInsnNode node, NodeHandlerParameters params) {
        switch (node.getType()) {
            case AbstractInsnNode.LDC_INSN:
                return new LdcNodeHandler((LdcInsnNode) node, params);
            case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
                return new InvokeDynamicNodeHandler((InvokeDynamicInsnNode) node, params);
            case AbstractInsnNode.METHOD_INSN:
                return new MethodNodeHandler((MethodInsnNode) node, params);
            case AbstractInsnNode.VAR_INSN:
                return new VarNodeHandler((VarInsnNode) node, params);
            case AbstractInsnNode.INSN:
                return new InsnNodeHandler((InsnNode) node, params);
            case AbstractInsnNode.FIELD_INSN:
                return new FieldNodeHandler((FieldInsnNode) node, params);
        }
        return null;
    }
}
