package de.leximon.fluidlogged.mixin.classes.world_interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {

    @Inject(method = "updateShape", at = @At("RETURN"))
    private void injectFluidTickingOnShapeUpdate(Direction direction, BlockState blockState, LevelAccessor level, BlockPos pos, BlockPos blockPos2, CallbackInfoReturnable<BlockState> cir) {
        Fluid fluid = level.getFluidState(pos).getType();
        if (fluid != Fluids.EMPTY)
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
    }

    @Inject(method = "use", at = @At("RETURN"))
    private void injectFluidTickingOnUse(Level level, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        BlockPos pos = blockHitResult.getBlockPos();
        Fluid fluid = level.getFluidState(pos).getType();
        if (fluid != Fluids.EMPTY)
            level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
    }

}
