package me.fengming.vaultpatcher_asm.core;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;
import java.util.function.Consumer;

public class VPMinecraftTransformer implements Consumer<ClassNode> {

    public VPMinecraftTransformer() {
        VaultPatcher.LOGGER.info("[VaultPatcher] Loading MinecraftTransformer");
    }

    private static void classTransform(ClassNode input) {
        if (input.name.equals("net/minecraft/util/text/StringTextComponent") || input.name.equals("net/minecraft/network/chat/TextComponent") /* Forge */
                || input.name.equals("net/minecraft/class_2585") /* Fabric */) {
            // TextComponent
            for (MethodNode method : input.methods) {
                if (method.name.equals("<init>")) {
                    for (ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                        AbstractInsnNode insn = it.next();
                        if (insn.getType() == AbstractInsnNode.FIELD_INSN) {
                            InsnList insnList = new InsnList();
                            insnList.add(new LdcInsnNode(input.name + "#<init>"));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                            method.instructions.insertBefore(insn, insnList);
                        }
                    }
                }
            }
        } else if (input.name.equals("net/minecraft/client/gui/Font") || input.name.equals("net/minecraft/client/gui/FontRenderer") /* Forge */
                    || input.name.equals("net/minecraft/class_327") /* Fabric */) {
            // Font
            for (MethodNode method : input.methods) {
                if (method.name.equals("m_92897_") || method.name.equals("func_228081_c_") /* Forge */ || method.name.equals("method_1724") /* Fabric */) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(new LdcInsnNode(input.name + "#renderText"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                }
            }
        }
    }

    // for Fabric
    @Override
    public void accept(ClassNode input) {
        classTransform(input);
    }
}
