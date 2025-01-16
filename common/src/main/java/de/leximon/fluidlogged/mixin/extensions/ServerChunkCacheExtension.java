package de.leximon.fluidlogged.mixin.extensions;

import net.minecraft.core.BlockPos;

public interface ServerChunkCacheExtension {

    void fluidChanged(BlockPos blockPos);

}
