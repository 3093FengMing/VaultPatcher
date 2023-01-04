package me.fengming.vaultpatcher.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;

@Mixin(value = FontRenderer.class, priority = Integer.MAX_VALUE)
public class FontRendererMixin {
    //GUI Transcription
    @ModifyArgs(
            method = {
                    "drawShadow(Lcom/mojang/blaze3d/matrix/MatrixStack;Ljava/lang/String;FFI)I",
                    "drawShadow(Lcom/mojang/blaze3d/matrix/MatrixStack;Ljava/lang/String;FFIZ)I",
                    "draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Ljava/lang/String;FFI)I"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawInternal(Ljava/lang/String;FFILnet/minecraft/util/math/vector/Matrix4f;ZZ)I"
            ))
    private void proxy_DrawInternal(Args args) {
        exportList.add(args.get(0));
        //Modify String
        String modifyString = ThePatcher.patch(args.get(0));
        modifyString = modifyString == null ? args.get(0) : modifyString;
        //Modify Pos
        //Coming Soon
        args.set(0, modifyString);
    }
}
