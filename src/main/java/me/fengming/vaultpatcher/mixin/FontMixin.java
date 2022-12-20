package me.fengming.vaultpatcher.mixin;

import com.google.common.collect.Lists;
import me.fengming.vaultpatcher.VaultPatcher;
import me.fengming.vaultpatcher.util.StackTrace;
import me.fengming.vaultpatcher.util.util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Arrays;

import static me.fengming.vaultpatcher.util.util.translationString;

@Mixin(Font.class)
public class FontMixin {
    //GUI Transcription
    @ModifyArg(
            method = "drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I"),
            index = 0)
    public String proxy_drawShadowFFI1(String p_92804_){
        /*
        These codes are in mctjl's lib AbstractLabel:

        Minecraft mc = Minecraft.getInstance();
        ......
        if (this.text == null) {
            this.mc.font.draw(matrixStack, "", (float)(x + dx + this.bounds.x), (float)(y + dy + this.bounds.y), col);
        } else {
            this.mc.font.draw(matrixStack, this.mc.font.plainSubstrByWidth(this.text, this.bounds.width), (float)(x + dx + this.bounds.x), (float)(y + dy + this.bounds.y), col);
        }

        Pay attention to the second param of draw function, it's always String.
        It is not excluded that someone uses FormattedCharSequence or Component.
        But I think he should use TranslatableComponent instead of TextComponent.
        So I choose not Mixin to drawInternal with FormattedCharSequence.

        */
        return translationString(p_92804_);
    }


    @ModifyArg(
            method = "drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I"),
            index = 0)
    public String proxy_drawShadowFFIZ(String p_92804_){
        return translationString(p_92804_);
    }


    @ModifyArg(
            method = "draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInternal(Ljava/lang/String;FFILcom/mojang/math/Matrix4f;ZZ)I"),
            index = 0)
    public String proxy_drawFFI1(String p_92804_){
        return translationString(p_92804_);
    }


}
