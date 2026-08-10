package com.github.tanconan.observing_eyeblossom.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.tanconan.observing_eyeblossom.client.RenderingEntityContext;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {

    @Inject(method = "updateForNonLiving", at = @At("HEAD"))
    private void observingEyeblossom$setContext(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Entity entity, CallbackInfo ci) {
        RenderingEntityContext.set(entity);
    }

    @Inject(method = "updateForNonLiving", at = @At("RETURN"))
    private void observingEyeblossom$clearContext(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Entity entity, CallbackInfo ci) {
        RenderingEntityContext.clear();
    }
}
