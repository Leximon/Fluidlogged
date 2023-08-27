package de.leximon.fluidlogged.commands.arguments;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Set;

public class FluidInput {

    private final FluidState state;
    private final Set<Property<?>> properties;

    public FluidInput(FluidState state, Set<Property<?>> set) {
        this.state = state;
        this.properties = set;
    }

    public FluidState getState() {
        return this.state;
    }

    public Set<Property<?>> getDefinedProperties() {
        return this.properties;
    }

    public boolean place(ServerLevel serverLevel, BlockPos blockPos, int flags) {
        return ((LevelExtension) serverLevel).setFluid(blockPos, this.state, flags);
    }
}
