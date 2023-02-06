package de.leximon.fluidlogged.mixin.classes.accessor;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StateHolder.class)
public interface StateAccessor {

    @Accessor("values")
    ImmutableMap<Property<?>, Comparable<?>> fl_getEntries();

}