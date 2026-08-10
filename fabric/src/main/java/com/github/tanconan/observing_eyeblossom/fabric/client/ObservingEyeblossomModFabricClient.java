package com.github.tanconan.observing_eyeblossom.fabric.client;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;
import com.github.tanconan.observing_eyeblossom.client.IsObserved;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.resources.Identifier;

public final class ObservingEyeblossomModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConditionalItemModelProperties.ID_MAPPER.put(
                Identifier.fromNamespaceAndPath(ObservingEyeblossomMod.MOD_ID, "observed"),
                IsObserved.MAP_CODEC);
    }
}
