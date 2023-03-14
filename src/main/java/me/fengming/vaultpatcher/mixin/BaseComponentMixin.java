package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import me.fengming.vaultpatcher.Utils;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BaseComponent.class)
public abstract class BaseComponentMixin {

    @Shadow
    @Final
    protected List<Component> siblings;

    @Shadow
    public abstract MutableComponent copy();

    @Shadow
    public abstract Style getStyle();

    @ModifyArg(
            method = "getVisualOrderText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/locale/Language;getVisualOrder(Lnet/minecraft/network/chat/FormattedText;)Lnet/minecraft/util/FormattedCharSequence;"
            )
    )
    private FormattedText proxy_getVisualOrder(FormattedText p_128116_) {
        if (p_128116_ instanceof TextComponent text) {
            String c = ThePatcher.patch(text.getContents(), "BaseComponent#getVisualOrder");
            return new TextComponent(c).setStyle(this.getStyle());
        }
        return p_128116_;
    }

    @Inject(method = "append", at = @At("HEAD"), cancellable = true)
    private void proxy_append(Component p_130585_, CallbackInfoReturnable<MutableComponent> cir) {
        if (p_130585_ instanceof TextComponent text) {
            String c = ThePatcher.patch(text.getContents(), "BaseComponent#append");
            this.siblings.add(new TextComponent(c).setStyle(text.getStyle()));
            cir.setReturnValue(this.copy());
        }
    }

}
