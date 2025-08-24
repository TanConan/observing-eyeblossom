package com.github.tanconan.observing_eyeblossom.fabric.client;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.ResourceLocation;

public final class ObservingEyeblossomModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConditionalItemModelProperties.ID_MAPPER.put(
                ResourceLocation.fromNamespaceAndPath(ObservingEyeblossomMod.MOD_ID, "observed"),
                IsObserved.MAP_CODEC);
    }
}
