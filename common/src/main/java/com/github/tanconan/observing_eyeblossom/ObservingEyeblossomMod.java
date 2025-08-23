package com.github.tanconan.observing_eyeblossom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;

public final class ObservingEyeblossomMod {
    public static final String MOD_ID = "observing_eyeblossom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<ResourceKey<Level>, Set<BlockPos>> viewedPositionsPerDimension = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<BlockPos>> openEyeblossomsPerDimension = new HashMap<>();

    public static void init() {
        TickEvent.SERVER_LEVEL_POST.register(ObservingEyeblossomMod::serverLevelPostTick);
    }

    private static void serverLevelPostTick(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();

        Set<BlockPos> viewedPositions = viewedPositionsPerDimension.computeIfAbsent(dimension, k -> new HashSet<>());
        Set<BlockPos> openEyeblossoms = openEyeblossomsPerDimension.computeIfAbsent(dimension, k -> new HashSet<>());

        level.players().forEach(player -> viewedPositions.addAll(getViewedBlockPositions(player)));

        for (BlockPos pos : viewedPositions) {
            if (level.getBlockState(pos).is(Blocks.OPEN_EYEBLOSSOM)) {
                openEyeblossoms.add(pos);
            } else if (level.getBlockState(pos).is(Blocks.CLOSED_EYEBLOSSOM)) {
                openEyeblossom(level, pos);
                openEyeblossoms.add(pos);
            }
        }

        openEyeblossoms.removeIf(pos -> {
            if (!viewedPositions.contains(pos)) {
                closeEyeblossom(level, pos);
                return true;
            }
            return false;
        });

        viewedPositions.clear();
    }

    private static List<BlockPos> getViewedBlockPositions(ServerPlayer player) {
        if (player.pick(10, 0, false) instanceof BlockHitResult blockHitResult)
            return List.of(blockHitResult.getBlockPos());
        return List.of();
    }

    private static void openEyeblossom(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.OPEN_EYEBLOSSOM.defaultBlockState());
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(Blocks.CLOSED_EYEBLOSSOM.defaultBlockState()));
        EyeblossomBlock.Type.OPEN.spawnTransformParticle(level, pos, level.getRandom());
        level.playSound(null, pos, EyeblossomBlock.Type.OPEN.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void closeEyeblossom(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.OPEN_EYEBLOSSOM))
            return;
        level.setBlockAndUpdate(pos, Blocks.CLOSED_EYEBLOSSOM.defaultBlockState());
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(Blocks.OPEN_EYEBLOSSOM.defaultBlockState()));
        EyeblossomBlock.Type.CLOSED.spawnTransformParticle(level, pos, level.getRandom());
        level.playSound(null, pos, EyeblossomBlock.Type.CLOSED.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
