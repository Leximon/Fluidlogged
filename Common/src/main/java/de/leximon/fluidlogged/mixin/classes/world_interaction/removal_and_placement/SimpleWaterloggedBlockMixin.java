package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWaterloggedBlock.class)
public interface SimpleWaterloggedBlockMixin {

    /**
     * @author Leximon (fluidlogged)
     * @reason allow any fluid to be placed
     */
    @Overwrite
    default boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return true; // allow any fluid to be placed, can be overridden to prevent this for specific blockStates e.g. SlabBlock
    }

}
