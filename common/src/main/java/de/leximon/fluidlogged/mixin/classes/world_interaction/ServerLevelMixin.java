package de.leximon.fluidlogged.mixin.classes.world_interaction;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState getFluidStateForRandomTick(BlockState instance, @Local ChunkPos chunkpos, @Local(ordinal = 5) int k, @Local LevelChunkSection levelChunkSection, @Local BlockPos randomBlockPos) {
        return levelChunkSection.getFluidState(randomBlockPos.getX() - chunkpos.getMinBlockX(), randomBlockPos.getY() - k, randomBlockPos.getZ() - chunkpos.getMinBlockZ());
    }
}
