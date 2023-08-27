package de.leximon.fluidlogged.mixin.extensions;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.world.level.material.FluidState;

public interface LevelChunkSectionExtension {

    Short2ObjectMap<FluidState> createAndSetFluidStatesMap();

    Short2ObjectMap<FluidState> getFluidStates();
    FluidState setFluidState(int x, int y, int z, FluidState fluidState);
}
