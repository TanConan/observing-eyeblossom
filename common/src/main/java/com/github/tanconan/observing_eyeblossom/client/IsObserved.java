package com.github.tanconan.observing_eyeblossom.client;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;
import com.github.tanconan.observing_eyeblossom.mixin.AbstractContainerScreenAccessor;
import com.mojang.serialization.MapCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public record IsObserved() implements ConditionalItemModelProperty {
    public static final MapCodec<IsObserved> MAP_CODEC = MapCodec.unit(new IsObserved());

    public IsObserved() {
    }

    public boolean get(ItemStack itemStack, ClientLevel clientLevel, LivingEntity livingEntity,
            int i, ItemDisplayContext itemDisplayContext) {
        // If the Itemstack is in the world
        if (itemDisplayContext == ItemDisplayContext.GROUND || itemDisplayContext == ItemDisplayContext.FIXED) {
            Entity itemEntity = itemStack.getEntityRepresentation();
            for (var player : clientLevel.players()) {
                double maxDist = ObservingEyeblossomMod.getMaxObservationDistance(player);
                double d2 = player.distanceToSqr(itemEntity);
                if (d2 > maxDist * maxDist)
                    continue;

                double tolerance2 = 0.25; // 0.5 * 0.5
                double cosThreshold2;
                if (d2 > tolerance2 && d2 != 0) {
                    cosThreshold2 = 1.0 - tolerance2 / d2;
                } else {
                    return true;
                }

                Vec3 toItem = itemEntity.position().subtract(player.getEyePosition(1.0F)).normalize();
                double dot = toItem.dot(player.getLookAngle());
                double dot2 = dot * dot;

                if (dot2 >= cosThreshold2) {
                    return true;
                }
            }
        }
        // If the Itemstack is in the players inventory
        if (livingEntity instanceof LocalPlayer localPlayer) {
            if (localPlayer.getInventory().getSelectedItem() == itemStack) {
                return true;
            }
            if (localPlayer.containerMenu != null) {
                ItemStack heldItem = localPlayer.containerMenu.getCarried();
                if (heldItem != null && !heldItem.isEmpty() && heldItem == itemStack) {
                    return true;
                }
            }
            var mc = Minecraft.getInstance();
            if (mc.screen instanceof AbstractContainerScreen<?> screen) {
                Slot hovered = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
                if (hovered != null && !hovered.getItem().isEmpty() && hovered.getItem() == itemStack) {
                    return true;
                }
            }
        }
        return false;
    }

    public MapCodec<IsObserved> type() {
        return MAP_CODEC;
    }
}
