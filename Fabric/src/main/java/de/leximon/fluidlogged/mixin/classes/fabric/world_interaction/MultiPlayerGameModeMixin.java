package de.leximon.fluidlogged.mixin.classes.fabric.world_interaction;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

//    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
//    private boolean redirectDestroyBlock(Level instance, BlockPos blockPos, BlockState blockState, int flags) {
//        return Fluidlogged.Internal.handleBlockRemoval(instance, blockPos, blockState, flags, 512);
//    }

    @Redirect(
            method = "destroyBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;createLegacyBlock()Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState modifyDestroyBlock(FluidState blockState, BlockPos blockPos) {
        return Fluidlogged.Internal.handleBlockRemoval(this.minecraft.level, blockPos, Block.UPDATE_ALL_IMMEDIATE, 512);
    }

}
