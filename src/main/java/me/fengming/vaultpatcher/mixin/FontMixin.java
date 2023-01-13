package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;

@Mixin(value = Font.class, priority = Integer.MAX_VALUE)
public class FontMixin {
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
    private String proxy_DrawInternal(String p_92804_) {
        return Objects.requireNonNullElse(ThePatcher.patch(p_92804_), p_92804_);
    }
}
