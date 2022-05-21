package de.leximon.fluidlogged.mixin.accessor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.block.state.StateHolder;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StateHolder.class)
public interface StateAccessor
{
    @Accessor("values")
    ImmutableMap<Property<?>, Comparable<?>> fluidlogged_getEntries();
}