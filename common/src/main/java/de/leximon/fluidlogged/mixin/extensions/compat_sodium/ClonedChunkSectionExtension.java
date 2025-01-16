package de.leximon.fluidlogged.mixin.extensions.compat_sodium;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import net.minecraft.world.level.material.FluidState;

public interface ClonedChunkSectionExtension {

    Int2ReferenceMap<FluidState> getFluidlogged$fluidData();

}
