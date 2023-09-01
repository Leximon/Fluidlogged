package de.leximon.fluidlogged.mixin.extensions.sodium_compat;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.world.level.material.FluidState;

public interface ClonedChunkSectionExtension {

    Int2ReferenceMap<FluidState> getFluidlogged$fluidData();

}
