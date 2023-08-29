package de.leximon.fluidlogged.mixin.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public interface ClientLevelExtension {

    void syncFluidState(BlockPos blockPos, FluidState fluidState);

}
