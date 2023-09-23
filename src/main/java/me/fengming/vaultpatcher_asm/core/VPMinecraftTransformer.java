package me.fengming.vaultpatcher_asm.core;

import com.google.common.collect.Sets;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import me.fengming.vaultpatcher_asm.ASMUtils;
import me.fengming.vaultpatcher_asm.VaultPatcher;
import net.minecraftforge.coremod.api.ASMAPI;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

public class VPMinecraftTransformer implements ITransformer<ClassNode> {

    VPMinecraftTransformer() {
        VaultPatcher.LOGGER.info("[VaultPatcher] Loading MinecraftTransformer");
    }

    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        if (input.name.equals("net/minecraft/util/text/StringTextComponent") || input.name.equals("net/minecraft/network/chat/TextComponent")) {
            // TextComponent
            for (MethodNode method : input.methods) {
                if (method.name.equals("<init>")) {
                    for (ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext(); ) {
                        AbstractInsnNode insn = it.next();
                        if (insn.getType() == AbstractInsnNode.FIELD_INSN) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;
                            InsnList insnList = new InsnList();
                            insnList.add(new LdcInsnNode(input.name + "#<init>"));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                            method.instructions.insertBefore(fieldInsnNode, insnList);
                        }
                    }
                }
            }
        } else if (input.name.equals("net/minecraft/client/gui/Font") || input.name.equals("net/minecraft/client/gui/FontRenderer")) {
            // Font
            for (MethodNode method : input.methods) {
                if (method.name.equals("m_92897_") || method.name.equals("func_228081_c_")) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(new LdcInsnNode(input.name + "#renderText"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/ASMUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                }
            }
        }
        return input;
    }

    @NotNull
    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @NotNull
    @Override
    public Set<Target> targets() {
        Set<Target> targets = new HashSet<>();
        // TextComponent
        targets.add(Target.targetClass("net.minecraft.util.text.StringTextComponent"));
        targets.add(Target.targetClass("net.minecraft.network.chat.TextComponent"));
        // Font
        targets.add(Target.targetClass("net.minecraft.client.gui.Font"));
        targets.add(Target.targetClass("net.minecraft.client.gui.FontRenderer"));
        return targets;
    }

}
