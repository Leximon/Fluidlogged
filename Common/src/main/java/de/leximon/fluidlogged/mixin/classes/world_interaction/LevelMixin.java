package de.leximon.fluidlogged.mixin.classes.world_interaction;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public class LevelMixin {

    @Redirect(method = "removeBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean redirectRemoveBlock(Level instance, BlockPos blockPos, BlockState blockState, int flags) {
        return Fluidlogged.Internal.handleBlockRemoval(instance, blockPos, blockState, flags, 512);
    }

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z"))
    private boolean redirectDestroyBlock(Level instance, BlockPos blockPos, BlockState blockState, int flags, int maxUpdateDepth) {
        return Fluidlogged.Internal.handleBlockRemoval(instance, blockPos, blockState, flags, maxUpdateDepth);
    }

}
