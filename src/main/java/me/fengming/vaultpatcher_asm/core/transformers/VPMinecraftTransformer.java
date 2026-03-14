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
        if (input.name.equals("net/minecraft/util/text/StringTextComponent") /* Forge 1.16.5- */
                || input.name.equals("net/minecraft/network/chat/TextComponent") /* Forge 1.16.5-1.18.2 */
                || input.name.equals("net/minecraft/network/chat/contents/LiteralContents") /* Forge 1.18.2-1.20.4 */
                || input.name.equals("net/minecraft/network/chat/contents/PlainTextContents$LiteralContents") /* Forge 1.20.6+ */
                || input.name.equals("net/minecraft/class_2585") /* Fabric */) {
            // TextComponent
            for (MethodNode method : input.methods) {
                if (!method.name.equals("<init>")) break;

                for (AbstractInsnNode insn : method.instructions) {
                    if (insn.getType() != AbstractInsnNode.FIELD_INSN) continue;

                    InsnList insnList = new InsnList();
                    insnList.add(new LdcInsnNode(input.name + "#<init>"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    method.instructions.insertBefore(insn, insnList);
                    VaultPatcher.debugInfo("[VaultPatcher] Dynamic Replace hook injected into {}", input.name);
                    break;
                }
            }
        } else if (input.name.equals("net/minecraft/client/gui/FontRenderer") /* Forge 1.16.5- */
                || input.name.equals("net/minecraft/client/gui/Font") /* Forge 1.16.5+ */
                || input.name.equals("net/minecraft/class_327") /* Fabric */) {
            // Font
            for (MethodNode method : input.methods) {
                if (method.name.equals("func_228081_c_") /* Forge 1.16.5- */
                        || method.name.equals("m_92897_") /* Forge 1.16.5-1.19.2 */
                        || method.name.equals("m_253026_") /* Forge 1.19.3 */
                        || method.name.equals("m_271978_") /* Forge 1.19.3-1.20.4 */
                        || method.name.equals("renderText") /* Forge 1.20.6-1.21.6 */
                        || method.name.equals("prepareText") /* Forge 1.21.6+ */
                        || method.name.equals("method_1724") /* Fabric */) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    insnList.add(new LdcInsnNode(input.name + "#renderText"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 1));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                    VaultPatcher.debugInfo("[VaultPatcher] Dynamic Replace hook injected into {}#{}", input.name, method.name);
                    break;
                }
            }
        // StringVistor (Fabric) / FormattedText (Forge)
        } else if (input.name.equals("net/minecraft/class_5348") /* Fabric */ ) {
            for (MethodNode method : input.methods) {
                if (method.name.equals("method_29430") || method.name.equals("method_29431")) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new LdcInsnNode(input.name + "#(Styled)Visitor)"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 0));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                    VaultPatcher.debugInfo("[VaultPatcher] Dynamic Replace hook injected into {}#{}", input.name, method.name);
                }
            }
        } else if (input.name.equals("net/minecraft/network/chat/FormattedText") /* Forge */ ) {
            for (MethodNode method : input.methods) {
                if (method.name.equals("m_130762_") || method.name.equals("m_130775_") /* Forge 1.17-1.20.6 */
                        || method.name.equals("of") /* Forge 1.20.6+ */ ) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new LdcInsnNode(input.name + "#(Styled)Visitor)"));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/fengming/vaultpatcher_asm/core/utils/DynamicReplaceUtils", "__mappingString", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 0));
                    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
                    VaultPatcher.debugInfo("[VaultPatcher] Dynamic Replace hook injected into {}#{}", input.name, method.name);
                }
            }
        }
    }

    // for Fabric
    @Override
    public void accept(ClassNode input) {
        VaultPatcher.debugInfo("[VaultPatcher] Loading MinecraftTransformer for class {}", input.name);
        classTransform(input);
    }
}
