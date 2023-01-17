package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;
import java.util.Optional;

import static me.fengming.vaultpatcher.VaultPatcher.exportList;

@Mixin(value = Font.class, priority = Integer.MAX_VALUE)
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
        return Objects.requireNonNullElse(ThePatcher.patch(p_92898_), p_92898_);
    }
}
