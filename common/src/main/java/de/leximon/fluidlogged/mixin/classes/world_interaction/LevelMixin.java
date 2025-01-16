package de.leximon.fluidlogged.mixin.classes.world_interaction;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public class LevelMixin {

    @Redirect(
            method = "removeBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState redirectDestroyBlock(FluidState instance, BlockPos blockPos, boolean bl) {
        return Fluidlogged.Internal.handleBlockRemoval((Level) (Object) this, blockPos, Block.UPDATE_ALL, 512);
    }

    @Redirect(
            method = "destroyBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState redirectDestroyBlock(FluidState fluidState, BlockPos blockPos, boolean bl, Entity entity, int maxUpdateDepth) {
        return Fluidlogged.Internal.handleBlockRemoval((Level) (Object) this, blockPos, Block.UPDATE_ALL, maxUpdateDepth);
    }


}
