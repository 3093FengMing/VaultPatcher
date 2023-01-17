package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BaseComponent.class)
public abstract class BaseComponentMixin {
    /*
    @Accessor("siblings")
    abstract List<Component> getSiblings();

    @Inject(method = "getSiblings", at = @At("HEAD"), cancellable = true)
    private void proxy_getSiblings(CallbackInfoReturnable<List<Component>> cir) {
        List<Component> list = this.getSiblings();
        if (!list.isEmpty()) {
            list.replaceAll(component -> {
                if (component instanceof TextComponent) {
                    return new TextComponent(ThePatcher.patch(component.getContents()));
                } else return component;
            });
        }
        cir.setReturnValue(list);
    }
    */

    @ModifyArg(
            method = "getVisualOrderText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/locale/Language;getVisualOrder(Lnet/minecraft/network/chat/FormattedText;)Lnet/minecraft/util/FormattedCharSequence;"
            )
    )
    private FormattedText proxy_getVisualOrder(FormattedText p_128116_) {
        if (p_128116_ instanceof TextComponent) {
            String c = ThePatcher.patch(p_128116_.getString());
            return new TextComponent(c);
        } else return p_128116_;
    }
}
