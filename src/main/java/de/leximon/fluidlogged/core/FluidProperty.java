package de.leximon.fluidlogged.core;

import net.minecraft.state.property.IntProperty;

public class FluidProperty extends IntProperty {

    protected FluidProperty(String name) {
        super(name, 0, FluidloggedConfig.fluidsLocked.size());
    }

    public static FluidProperty of(String name) {
        return new FluidProperty(name);
    }
}
