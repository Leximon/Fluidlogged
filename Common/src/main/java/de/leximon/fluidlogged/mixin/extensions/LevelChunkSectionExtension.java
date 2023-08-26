package de.leximon.fluidlogged.mixin.extensions;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;
import java.util.Map;

public interface LevelChunkSectionExtension {

    Short2ObjectMap<FluidState> getFluidStates();
    void setFluidStates(Short2ObjectMap<FluidState> fluidStates);
    void setFluidState(int x, int y, int z, FluidState fluidState);
}
