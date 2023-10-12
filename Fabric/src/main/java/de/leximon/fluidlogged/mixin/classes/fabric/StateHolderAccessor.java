package de.leximon.fluidlogged.mixin.classes.fabric;

import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Function;

@Mixin(StateHolder.class)
public interface StateHolderAccessor {

    @Accessor("PROPERTY_ENTRY_TO_STRING_FUNCTION")
    static Function<Map.Entry<Property<?>, Comparable<?>>, String> fluidlogged$PROPERTY_ENTRY_TO_STRING_FUNCTION() {
        throw new AssertionError();
    }

}
