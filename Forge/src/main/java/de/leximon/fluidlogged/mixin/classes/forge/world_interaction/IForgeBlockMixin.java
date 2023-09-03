package de.leximon.fluidlogged.mixin.classes.forge.world_interaction;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IForgeBlock.class)
public interface IForgeBlockMixin {

    @Shadow(remap = false) Block self();


    // probably a very stupid but I don't know what to do else
    /**
     * @author Leximon
     * @reason place flowing fluid instead of always placing the full source block
     */
    @Overwrite(remap = false)
    default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        self().playerWillDestroy(level, pos, state, player);
        return level.setBlock(pos, Fluidlogged.Internal.handleBlockRemoval(level, pos, Block.UPDATE_ALL_IMMEDIATE, 512), level.isClientSide ? 11 : 3);
    }

}
