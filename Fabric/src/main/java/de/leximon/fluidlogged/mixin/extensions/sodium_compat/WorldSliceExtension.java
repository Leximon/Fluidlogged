package de.leximon.fluidlogged.mixin.extensions.sodium_compat;

import net.minecraft.world.level.material.FluidState;

public interface WorldSliceExtension {

    FluidState fluidlogged$getFluidState(int x, int y, int z);

}
