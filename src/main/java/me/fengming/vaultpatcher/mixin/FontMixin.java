package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = Font.class)
public class FontMixin {

    // String
    @ModifyArg(
            method = "drawInternal(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZIIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;renderText(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)F"
            ),
            index = 0
    )
    private String proxy_renderText(String p_92898_) {
        String c = ThePatcher.patch(p_92898_, "Font#drawInternal(String;float;float;int;boolean;Matrix4f;MultiBufferSource;boolean;int;int;boolean)");
        if (c != null && !c.equals("")) {
            return c;
        }
        return p_92898_;
    }
}
