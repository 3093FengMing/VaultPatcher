package me.fengming.vaultpatcher_asm.core.transformers;

import me.fengming.vaultpatcher_asm.VaultPatcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

public class VPMinecraftTransformer implements Consumer<ClassNode> {

    public VPMinecraftTransformer() {
        VaultPatcher.debugInfo("[VaultPatcher] Loading MinecraftTransformer");
    }

    private static void classTransform(ClassNode input) {
        switch (input.name) {
            case "net/minecraft/util/text/StringTextComponent": // SRG 1.16.5-
            case "net/minecraft/network/chat/TextComponent":    // MojMaps 1.17-1.18.2
            case "net/minecraft/network/chat/contents/LiteralContents":     // MojMaps 1.18.2-1.20.2
            case "net/minecraft/network/chat/contents/PlainTextContents$LiteralContents":   // MojMaps 1.20.3+
            case "net/minecraft/class_2585":                    // Intermediary 1.20.2-
            case "net/minecraft/class_8828$class$2585":
                // TextComponent
                for (MethodNode method : input.methods) {
                    if (!method.name.equals("<init>")) break;

                    for (AbstractInsnNode insn : method.instructions) {
                        if (insn.getType() != AbstractInsnNode.FIELD_INSN) continue;

                        InsnList insnList = new InsnList();
                        insnList.add(new LdcInsnNode(input.name + "#<init>"));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                        method.instructions.insertBefore(insn, insnList);
                        break;
                    }
                }
                break;
            case "net/minecraft/client/gui/FontRenderer":       // SRG 1.16.5-
            case "net/minecraft/client/gui/Font":               // MojMaps 1.17+
            case "net/minecraft/class_327":                     // Intermediary
                // Font
                for (MethodNode method : input.methods) {
                    boolean matches = false;
                    switch (method.name) {
                        // before 1.21.5
                        // float renderText(
                        //      String text, float x, float y, int color, boolean dropShadow,
                        //      Matrix4f pose, MultiBufferSource bufferSource, DisplayMode displayMode,
                        //      int backgroundColor, int packedLightCoords, boolean inverseDepth
                        // )
                        case "func_228081_c_":  // SRG 1.16.5-
                        case "m_92897_":        // SRG 1.17-1.19.2
                        case "m_253026_":       // SRG 1.19.3
                        case "m_271978_":       // SRG 1.19.3+
                        case "renderText":      // MojMaps 1.21.5-
                        case "method_1724":     // Intermediary 1.21.5-
                        // No longer available in post-1.21.5
                            matches = true;
                            break;
                    }
                    if (!matches) continue;

                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(new LdcInsnNode(input.name + "#renderText"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                    break;
                }
                break;
        }
    }

    // for Fabric
    @Override
    public void accept(ClassNode input) {
        VaultPatcher.debugInfo("[VaultPatcher] Loading MinecraftTransformer for class {}", input.name);
        classTransform(input);
    }
}
