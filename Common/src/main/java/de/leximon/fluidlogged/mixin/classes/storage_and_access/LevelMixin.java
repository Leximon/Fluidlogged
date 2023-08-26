package de.leximon.fluidlogged.mixin.classes.storage_and_access;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelExtension {


    @Shadow public abstract LevelChunk getChunkAt(BlockPos blockPos);

    @Override
    public boolean setFluid(BlockPos blockPos, FluidState fluidState) {
        LevelChunk levelChunk = this.getChunkAt(blockPos);
        ((LevelChunkExtension) levelChunk).setFluidState(blockPos, fluidState);
        return false;
    }
}
