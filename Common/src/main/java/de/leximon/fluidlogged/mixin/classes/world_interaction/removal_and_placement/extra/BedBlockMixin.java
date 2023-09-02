package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement.extra;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Redirect(method = "setPlacedBy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean redirectFluidPlacementByBedHead(Level instance, BlockPos blockPos, BlockState blockState, int flags) {
        return ((LevelExtension) instance).setBlockAndInsertFluidIfPossible(blockPos, blockState, flags);
    }

}
