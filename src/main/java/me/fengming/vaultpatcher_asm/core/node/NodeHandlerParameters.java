package me.fengming.vaultpatcher_asm.core.node;

import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;

public class NodeHandlerParameters {
    public boolean disableLocal;
    public boolean disableVariable;

    public ClassNode classNode;
    public MethodNode methodNode;

    public HashMap<Integer, String> localVariableMap;
    public TranslationInfo info;
    public int ordinal = 0;

    public NodeHandlerParameters(boolean disableLocal, boolean disableVariable, ClassNode classNode, MethodNode methodNode, HashMap<Integer, String> localVariableMap, TranslationInfo info) {
        this.disableLocal = disableLocal;
        this.disableVariable = disableVariable;
        this.classNode = classNode;
        this.methodNode = methodNode;
        this.localVariableMap = localVariableMap;
        this.info = info;
    }

    public void addOrdinal() {
        this.ordinal++;
    }
}
