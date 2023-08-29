package de.leximon.fluidlogged.mixin.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.ApiStatus;

public interface LevelExtension {

    @ApiStatus.Internal
    boolean setFluid(BlockPos blockPos, FluidState fluidState, int flags, int maxUpdateDepth);

    default boolean setFluid(BlockPos blockPos, FluidState fluidState, int flags) {
        return LevelExtension.this.setFluid(blockPos, fluidState, flags, 512);
    }

    default void sendFluidUpdated(BlockPos blockPos, int flags) { }

    @ApiStatus.Internal
    default void fluidlogged$setBlocksDirty(int x1, int y1, int z1, int x2, int y2, int z2) { }

}
