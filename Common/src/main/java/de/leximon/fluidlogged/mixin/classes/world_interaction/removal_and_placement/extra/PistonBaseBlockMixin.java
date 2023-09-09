package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement.extra;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(
            method = "moveBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I",
                    ordinal = 3
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectFluidRemoval(
            Level level, BlockPos blockPos, Direction direction, boolean bl, CallbackInfoReturnable<Boolean> cir,
            BlockPos blockPos2, PistonStructureResolver pistonStructureResolver, Map<BlockPos, BlockState> map, List list, List list2, List list3, BlockState[] blockStates, Direction direction2, int j
    ) {
        for (BlockPos pos : map.keySet()) {
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty())
                continue;

            ((LevelExtension) level).setFluid(pos, Fluids.EMPTY.defaultFluidState(), Block.UPDATE_ALL);
        }
    }

}
