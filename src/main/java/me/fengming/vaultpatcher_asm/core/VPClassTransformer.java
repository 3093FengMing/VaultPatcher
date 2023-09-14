package me.fengming.vaultpatcher_asm.core;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.Utils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import me.fengming.vaultpatcher_asm.config.DebugMode;
import me.fengming.vaultpatcher_asm.config.Pairs;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.config.VaultPatcherConfig;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.*;

public class VPClassTransformer implements ITransformer<ClassNode> {

    private final TranslationInfo translationInfo;
    public VPClassTransformer(TranslationInfo info) {
        this.translationInfo = info;
        VaultPatcher.LOGGER.warn("[VaultPatcher] Loading VPTransformer!");
    }

    private static void methodReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        for (MethodNode method : input.methods) {
            String methodName = info.getTargetClassInfo().getMethod();
            if (methodName.isEmpty() || methodName.equals(method.name)) {
                // Initial Local Variable Map
                final HashMap<Integer, String> localVariableMap = new HashMap<>();
                boolean localVarEnable = false;
                if (method.localVariables != null) {
                    localVarEnable = true;
                    method.localVariables.stream().filter(node -> node.desc.equals("Ljava/lang/String;")).forEach(node -> localVariableMap.put(node.index, node.name));
                }

                for (ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode instruction = it.next();
                    Pairs pairs = info.getPairs();

                    // Start checking instructions and replacing it here
                    if (instruction.getType() == AbstractInsnNode.LDC_INSN) {
                        // String Constants
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) instruction;
                        if (ldcInsnNode.cst instanceof String) {
                            String v = Utils.matchPairs(pairs, (String) ldcInsnNode.cst);
                            Utils.printDebugIndo((String) ldcInsnNode.cst, "ASMTransformMethod-Ldc", v, input.name, debug);
                            ldcInsnNode.cst = v;
                        }
                    } else if (instruction.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN) {
                        // String Concatenation (jdk8+)
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        if (invokeDynamicInsnNode.name.equals("makeConcatWithConstants")) {
                            for (int i = 0; i < invokeDynamicInsnNode.bsmArgs.length; i++) {
                                if (invokeDynamicInsnNode.bsmArgs[i] instanceof String) {
                                    String str = (String) invokeDynamicInsnNode.bsmArgs[i];
                                    String[] parts = str.split("\u0001", -1);
                                    for (int j = 0; j < parts.length; j++) {
                                        parts[j] = Utils.matchPairs(pairs, parts[j]);
                                    }
                                    String v = String.join("\u0001", parts);
                                    Utils.printDebugIndo(str, "ASMTransformMethod-InvokeDynamic", v.replace("\u0001", "<p>"), input.name, debug);
                                    invokeDynamicInsnNode.bsmArgs[i] = v;
                                }
                            }
                        }
                    } else if (instruction.getType() == AbstractInsnNode.METHOD_INSN) { // Method Insn
                        MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                        if (methodInsnNode.desc.endsWith(")Ljava/lang/String;") && !methodInsnNode.name.equals("__replaceMethod")) {
                            // For any method that returns String
                            if (matchLocal(info, methodInsnNode.name, true)) {
                                InsnList list = new InsnList();
                                // array
                                list.add(__makeNewArray(info.getPairs().getPairs().entrySet()));
                                // to pairs
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__pairsByArrays", "([Ljava/lang/String;[Ljava/lang/String;)Lme/fengming/vaultpatcher_asm/config/Pairs;", false));
                                // call Utils.__replaceMethod(source, key, value);
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__replaceMethod", "(Ljava/lang/String;Lme/fengming/vaultpatcher_asm/config/Pairs;)Ljava/lang/String;", false));
                                method.instructions.insert(methodInsnNode, list);
                                Utils.printDebugIndo("Unknown At Runtime", "ASMTransformMethod-InsertMethodCalled", "Unknown At Runtime", input.name, debug);
                            }
                        }
                    } else if (localVarEnable && instruction.getType() == AbstractInsnNode.VAR_INSN) { // Var Insn
                        // For any local variable with String type
                        VarInsnNode varInsnNode = (VarInsnNode) instruction;
                        if (varInsnNode.getOpcode() == Opcodes.ASTORE) {
                            if (matchLocal(info, localVariableMap.getOrDefault(varInsnNode.var, null), false)) {
                                InsnList list = new InsnList();
                                // array
                                list.add(__makeNewArray(info.getPairs().getPairs().entrySet()));
                                // to pairs
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__pairsByArrays", "([Ljava/lang/String;[Ljava/lang/String;)Lme/fengming/vaultpatcher_asm/config/Pairs;", false));
                                // call Utils.__replaceMethod(source, key, value);
                                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__replaceMethod", "(Ljava/lang/String;Lme/fengming/vaultpatcher_asm/config/Pairs;)Ljava/lang/String;", false));
                                method.instructions.insert(varInsnNode, list);
                                Utils.printDebugIndo("Unknown At Runtime", "ASMTransformMethod-InsertLocalVariableStore", "Unknown At Runtime", input.name, debug);
                            }
                        }
                    }
                }
            }
        }
    }

    public static InsnList __makeNewArray(Set<Map.Entry<String, String>> set) {
        // insert two arrays (keys & values)
        int size = set.size();

        InsnList list1 = new InsnList();
        list1.add(__index(size));
        list1.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));

        InsnList list2 = new InsnList();
        list2.add(__index(size));
        list2.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));

        int index = 0;
        for (Map.Entry<String, String> entry : set) {
            list1.add(new InsnNode(Opcodes.DUP));
            list1.add(__index(index));
            list1.add(new LdcInsnNode(entry.getKey()));
            list1.add(new InsnNode(Opcodes.AASTORE));

            list2.add(new InsnNode(Opcodes.DUP));
            list2.add(__index(index));
            list2.add(new LdcInsnNode(entry.getValue()));
            list2.add(new InsnNode(Opcodes.AASTORE));

            index++;
        }

        InsnList ret = new InsnList();
        ret.add(list1);
        ret.add(list2);

        return ret;
    }

    private static AbstractInsnNode __index(int i) {
        return i > 5 ? new IntInsnNode(Opcodes.BIPUSH, i) : new InsnNode(Opcodes.ICONST_0 + i);
    }

    public static boolean matchLocal(TranslationInfo info, String name, boolean isMethod) {
        if (name == null) return false;
        String l = info.getTargetClassInfo().getLocal();
        if (l.isEmpty()) return false;
        if ((l.charAt(0) == 'M' && isMethod) || (l.charAt(0) == 'V' && !isMethod)) {
            return l.substring(1).equals(name);
        }
        return false;
    }

    private static void fieldReplace(ClassNode input, TranslationInfo info, DebugMode debug) {
        Pairs pairs = info.getPairs();
        for (FieldNode field : input.fields) {
            if (field.value instanceof String) {
                String o = (String) field.value;
                String v = Utils.matchPairs(pairs, o);
                Utils.printDebugIndo(o, "ASMTransformField", v, input.name, debug);
                field.value = v;
            }
        }
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        DebugMode debug = VaultPatcherConfig.getDebugMode();
        if (translationInfo == null) {
            for (TranslationInfo info : Utils.translationInfos) {
                if (info.getTargetClassInfo().getName().isEmpty() || input.name.equals(Utils.rawPackage(info.getTargetClassInfo().getName()))) {
                    methodReplace(input, info, debug);
                    fieldReplace(input, info, debug);
                }
            }
        } else {
            if (this.translationInfo.getTargetClassInfo().getName().isEmpty() || input.name.equals(Utils.rawPackage(this.translationInfo.getTargetClassInfo().getName()))) {
                methodReplace(input, this.translationInfo, debug);
                fieldReplace(input, this.translationInfo, debug);
            }
        }
        return input;
    }

    @Override
    public @org.jetbrains.annotations.NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        if (translationInfo == null) {
            targets.addAll(Utils.addConfigApplyMods()); // May cause unnecessary resource waste
            targets.addAll(Utils.addConfigClasses());
        } else {
            Target t = Utils.addTargetClasses(this.translationInfo);
            if (t != null) targets.add(t);
        }
        targets.iterator().forEachRemaining(t -> System.out.println("t = " + t.getClassName()));
        return targets;
    }

}
