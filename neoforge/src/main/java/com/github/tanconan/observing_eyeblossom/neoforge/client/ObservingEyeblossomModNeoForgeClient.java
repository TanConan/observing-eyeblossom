package com.github.tanconan.observing_eyeblossom.neoforge.client;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;
import com.github.tanconan.observing_eyeblossom.client.IsObserved;

import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;

@EventBusSubscriber(modid = ObservingEyeblossomMod.MOD_ID, value = Dist.CLIENT)
public class ObservingEyeblossomModNeoForgeClient {

    @SubscribeEvent
    public static void onRegisterConditionalProps(RegisterConditionalItemModelPropertyEvent event) {
        Identifier id = Identifier.fromNamespaceAndPath(ObservingEyeblossomMod.MOD_ID, "observed");
        event.register(id, IsObserved.MAP_CODEC);
    }
}
