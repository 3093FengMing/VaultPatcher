package me.fengming.vaultpatcher.mixin;

import me.fengming.vaultpatcher.ThePatcher;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BaseComponent.class)
public abstract class BaseComponentMixin {
    @Accessor("siblings")
    abstract List<Component> getSiblings();

    @Inject(method = "getSiblings", at = @At("HEAD"), cancellable = true)
    private void proxy_getSiblings(CallbackInfoReturnable<List<Component>> cir) {
        List<Component> list = this.getSiblings();
        List<Component> rList = Lists.newArrayList();
        if (!list.isEmpty()) {
            for (Component str : list) {
                rList.add(new TextComponent(ThePatcher.patch(str.getContents())));
            }
        }
        cir.setReturnValue(rList);
    }
}
