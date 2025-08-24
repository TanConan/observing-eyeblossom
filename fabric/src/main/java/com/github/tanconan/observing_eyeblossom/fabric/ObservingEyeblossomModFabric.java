package com.github.tanconan.observing_eyeblossom.fabric;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class ObservingEyeblossomModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(ObservingEyeblossomMod::serverLevelPostTick);
    }
}
