package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import me.fengming.vaultpatcher.VaultPatcher;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(Font.class)
public class FontMixin {
    // fixme: which mixin is useful?
    //GUI Transcription
    @ModifyArg(
            method = {
                    "drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I",
                    "drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFIZ)I",
                    "draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I"),
            index = 0)
    private String proxyDraw(String p_92804_) {
        VaultPatcher.LOGGER.info(p_92804_);
        return Objects.requireNonNullElse(ThePatcher.patch(p_92804_), p_92804_);
    }
}
