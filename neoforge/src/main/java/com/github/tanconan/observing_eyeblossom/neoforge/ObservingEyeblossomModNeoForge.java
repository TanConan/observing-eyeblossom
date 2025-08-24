package com.github.tanconan.observing_eyeblossom.neoforge;

import com.github.tanconan.observing_eyeblossom.ObservingEyeblossomMod;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = ObservingEyeblossomMod.MOD_ID)
public class ObservingEyeblossomModNeoForge {

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.hasTime()) {
            ObservingEyeblossomMod.serverLevelPostTick(serverLevel);
        }
    }
}
