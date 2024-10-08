package me.fengming.vaultpatcher_asm.core.node;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;

public class NodeHandlerParameters {
    public boolean disableLocal;
    public boolean disableLocalVariable;

    public ClassNode classNode;
    public MethodNode methodNode;

    public HashMap<Integer, String> localVariableMap;
    public TranslationInfo info;
    public int ordinal = 0;

    public NodeHandlerParameters(boolean disableLocal, boolean disableLocalVariable, ClassNode classNode, MethodNode methodNode, HashMap<Integer, String> localVariableMap, TranslationInfo info) {
        this.disableLocal = disableLocal;
        this.disableLocalVariable = disableLocalVariable;
        this.classNode = classNode;
        this.methodNode = methodNode;
        this.localVariableMap = localVariableMap;
        this.info = info;
    }

    public void addOrdinal() {
        this.ordinal++;
    }

    @Override
    public String toString() {
        return "NodeHandlerParameters{" +
                "disableLocal=" + disableLocal +
                ", disableLocalVariable=" + disableLocalVariable +
                ", classNode=" + classNode.name +
                ", methodNode=" + methodNode.name +
                ", localVariableMap=" + localVariableMap +
                ", info=" + info +
                ", ordinal=" + ordinal +
                '}';
    }
}
