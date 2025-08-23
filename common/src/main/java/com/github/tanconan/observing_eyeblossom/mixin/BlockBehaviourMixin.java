package com.github.tanconan.observing_eyeblossom.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {

    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void onGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction,
            CallbackInfoReturnable<Integer> cir) {
        if (state.is(Blocks.OPEN_EYEBLOSSOM) && direction != Direction.DOWN && direction != Direction.UP)
            cir.setReturnValue(15);
    }

    @Inject(method = "isSignalSource", at = @At("HEAD"), cancellable = true)
    private void onIsSignalSource(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.OPEN_EYEBLOSSOM) || state.is(Blocks.CLOSED_EYEBLOSSOM))
            cir.setReturnValue(true);
    }
}
