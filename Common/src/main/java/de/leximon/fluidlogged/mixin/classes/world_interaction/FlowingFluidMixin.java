package de.leximon.fluidlogged.mixin.classes.world_interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {

    @Unique
    private BlockPos fluidloggedBlockPos;

    @Redirect(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState injectFluidBehavior(Level instance, BlockPos blockPos) {
        this.fluidloggedBlockPos = blockPos;
        return instance.getBlockState(blockPos);
    }

    @Redirect(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState injectFluidBehavior(BlockState instance, Level level, BlockPos blockPos, BlockState blockState) {
        return level.getFluidState(fluidloggedBlockPos);
    }

    @Redirect(method = "method_15755", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"))
    private static FluidState injectFluidBehavior2(BlockState instance, LevelReader level, BlockPos pos, short s) {
        return level.getFluidState(pos);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
    private boolean injectFluidBehavior3(FluidState instance, Level level, BlockPos blockPos, FluidState fluidState) {
        return instance.isSource() || !(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock);
    }

//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"))
//    private boolean injectFluidBehavior3(FluidState instance, Level level, BlockPos blockPos, FluidState fluidState) {
//        return instance.isEmpty() && level.getBlockState(blockPos).getBlock() instanceof LiquidBlock;
//    }

//    @Inject(method = "spreadTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"), cancellable = true)
//    private void injectDoNotDestroyMotionBlockingBlocks(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
//        if (blockState.blocksMotion())
//            ci.cancel();
//    }
}
