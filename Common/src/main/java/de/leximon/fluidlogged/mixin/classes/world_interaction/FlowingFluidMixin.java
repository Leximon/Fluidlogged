package de.leximon.fluidlogged.mixin.classes.world_interaction;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    @Unique
    private BlockPos fluidloggedBlockPos;

    @Redirect(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState redirectFluidBehavior(Level instance, BlockPos blockPos) {
        this.fluidloggedBlockPos = blockPos;
        return instance.getBlockState(blockPos);
    }

    @Redirect(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState redirectFluidBehavior(BlockState instance, Level level, BlockPos blockPos, BlockState blockState) {
        return level.getFluidState(fluidloggedBlockPos);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = { "method_15755", "lambda$getSlopeDistance$1", "m_284125_" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"
            ),
            require = 1
    )
    private static FluidState redirectFluidBehavior2(BlockState instance, LevelReader level, BlockPos pos, short s) {
        return level.getFluidState(pos);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = { "method_15734", "lambda$getSpread$3", "m_284124_" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"
            ),
            require = 1
    )
    private static FluidState redirectFluidBehavior3(BlockState instance, Level level, BlockPos pos, short s) {
        return level.getFluidState(pos);
    }

    @Inject(method = "spreadTo", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.AFTER, ordinal = 0), cancellable = true)
    private void injectSpreadTo(LevelAccessor level, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo ci) {
        if (Fluidlogged.isFluidPermeable(blockState) || fluidState.isSource())
            ((LevelExtension) level).setFluid(blockPos, fluidState, Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
        ci.cancel();
    }

    @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
    @ModifyConstant(
            method = "spreadTo",
            constant = @Constant(ordinal = 0, classValue = LiquidBlockContainer.class)
    )
    private boolean redirectBypassLiquidBlockContainerCheck(
            Object reference, Class<LiquidBlockContainer> clazz,
            LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState
    ) {
        return Fluidlogged.canPlaceFluid(levelAccessor, blockPos, blockState, fluidState.getType())
                && levelAccessor.getFluidState(blockPos).isEmpty();
    }

    @Inject(method = "canHoldFluid", at = @At("HEAD"), cancellable = true)
    private void redirectBypassLiquidBlockContainerCheck2(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (Fluidlogged.canPlaceFluid(blockGetter, blockPos, blockState, fluid)
                && blockGetter.getFluidState(blockPos).isEmpty())
            cir.setReturnValue(Fluidlogged.isFluidPermeable(blockState));
    }

    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private boolean redirectTickSetBlock(
            Level instance, BlockPos blockPos, BlockState blockState, int flags,
            Level level, BlockPos blockPos2, FluidState fluidState
    ) {
        BlockState prevBlock = level.getBlockState(blockPos2);

        if (!Fluidlogged.isFluidloggable(prevBlock)) {
            ((LevelExtension) level).setFluid(blockPos, Fluids.EMPTY.defaultFluidState(), flags);
            return instance.setBlock(blockPos, blockState, flags);
        }

        ((LevelExtension) level).setFluid(blockPos, fluidState, flags);
        return false;
    }

    @Inject(
            method = "isSolidFace",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void injectShapeIndependentFluidPermeableCheck(BlockGetter blockGetter, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (Fluidlogged.isShapeIndependentFluidPermeable(blockState))
            cir.setReturnValue(false);
    }

    @Inject(
            method = "canPassThroughWall",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectShapeIndependentFluidPermeableCheck2(Direction direction, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2, CallbackInfoReturnable<Boolean> cir) {
        if (Fluidlogged.isShapeIndependentFluidPermeable(blockState)
                || Fluidlogged.isShapeIndependentFluidPermeable(blockState2))
            cir.setReturnValue(true);
    }
}
