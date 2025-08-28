package com.github.tanconan.observing_eyeblossom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;

public final class ObservingEyeblossomMod {
    public static final String MOD_ID = "observing_eyeblossom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final double MAX_BLOSSOM_OBSERVATION_DISTANCE = 16;
    public static final double SPYGLAS_BLOSSOM_OBSERVATION_DISTANCE_MULT = 10;

    private static final Map<ResourceKey<Level>, Set<BlockPos>> viewedPositionsPerDimension = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<BlockPos>> openEyeblossomsPerDimension = new HashMap<>();

    public static double getMaxObservationDistance(Player player) {
        return (player.isUsingItem() && player.getUseItem().is(Items.SPYGLASS)
                ? SPYGLAS_BLOSSOM_OBSERVATION_DISTANCE_MULT
                : 1) * MAX_BLOSSOM_OBSERVATION_DISTANCE;
    }

    public static void serverLevelPostTick(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();

        Set<BlockPos> viewedPositions = viewedPositionsPerDimension.computeIfAbsent(dimension, k -> new HashSet<>());
        Set<BlockPos> openEyeblossoms = openEyeblossomsPerDimension.computeIfAbsent(dimension, k -> new HashSet<>());

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator())
                continue;
            viewedPositions.addAll(getViewedBlockPositions(player));
        }

        for (BlockPos pos : viewedPositions) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(Blocks.OPEN_EYEBLOSSOM) || blockState.is(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
                openEyeblossoms.add(pos);
            } else if (blockState.is(Blocks.CLOSED_EYEBLOSSOM) || blockState.is(Blocks.POTTED_CLOSED_EYEBLOSSOM)) {
                openEyeblossom(level, pos, blockState);
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

    private static Set<BlockPos> getViewedBlockPositions(ServerPlayer player) {
        double distance = getMaxObservationDistance(player);
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 end = start.add(player.getLookAngle().scale(distance));
        return traverseBlocks(start, end);
    }

    private static void openEyeblossom(ServerLevel level, BlockPos pos, BlockState blockState) {
        BlockState wantedState;
        if (blockState.is(Blocks.CLOSED_EYEBLOSSOM)) {
            wantedState = Blocks.OPEN_EYEBLOSSOM.defaultBlockState();
        } else {
            wantedState = Blocks.POTTED_OPEN_EYEBLOSSOM.defaultBlockState();
        }
        level.setBlockAndUpdate(pos, wantedState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(blockState));
        EyeblossomBlock.Type.OPEN.spawnTransformParticle(level, pos, level.getRandom());
        level.playSound(null, pos, EyeblossomBlock.Type.OPEN.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void closeEyeblossom(ServerLevel level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        BlockState wantedState;
        if (blockState.is(Blocks.OPEN_EYEBLOSSOM)) {
            wantedState = Blocks.CLOSED_EYEBLOSSOM.defaultBlockState();
        } else if (blockState.is(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
            wantedState = Blocks.POTTED_CLOSED_EYEBLOSSOM.defaultBlockState();
        } else {
            return;
        }
        level.setBlockAndUpdate(pos, wantedState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(blockState));
        EyeblossomBlock.Type.CLOSED.spawnTransformParticle(level, pos, level.getRandom());
        level.playSound(null, pos, EyeblossomBlock.Type.CLOSED.longSwitchSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static Set<BlockPos> traverseBlocks(Vec3 start, Vec3 end) {
        Set<BlockPos> visited = new HashSet<>();

        double x = start.x;
        double y = start.y;
        double z = start.z;

        int bx = (int) Mth.floor(x);
        int by = (int) Mth.floor(y);
        int bz = (int) Mth.floor(z);

        int tx = (int) Mth.floor(end.x);
        int ty = (int) Mth.floor(end.y);
        int tz = (int) Mth.floor(end.z);

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        int stepX = (dx > 0) ? 1 : (dx < 0) ? -1 : 0;
        int stepY = (dy > 0) ? 1 : (dy < 0) ? -1 : 0;
        int stepZ = (dz > 0) ? 1 : (dz < 0) ? -1 : 0;

        double tMaxX = intBound(x, dx);
        double tMaxY = intBound(y, dy);
        double tMaxZ = intBound(z, dz);

        double tDeltaX = (stepX != 0) ? 1.0 / Math.abs(dx) : Double.MAX_VALUE;
        double tDeltaY = (stepY != 0) ? 1.0 / Math.abs(dy) : Double.MAX_VALUE;
        double tDeltaZ = (stepZ != 0) ? 1.0 / Math.abs(dz) : Double.MAX_VALUE;

        int maxSteps = (int) ((Math.abs(dx) + Math.abs(dy) + Math.abs(dz)) * 3);

        for (int i = 0; i < maxSteps; i++) {
            visited.add(new BlockPos(bx, by, bz));

            if (bx == tx && by == ty && bz == tz)
                break;

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    bx += stepX;
                    tMaxX += tDeltaX;
                } else {
                    bz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    by += stepY;
                    tMaxY += tDeltaY;
                } else {
                    bz += stepZ;
                    tMaxZ += tDeltaZ;
                }
            }
        }

        return visited;
    }

    private static double intBound(double s, double ds) {
        if (ds > 0) {
            return (Mth.ceil(s) - s) / ds;
        } else if (ds < 0) {
            return (s - Mth.floor(s)) / -ds;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }
}
