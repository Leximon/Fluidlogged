package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ConfigDefaults {

    public static final List<String> FLUIDLOGGABLE_BLOCKS = ImmutableList.of(
            "#minecraft:pressure_plates",
            "#minecraft:doors"
    );

    public static final boolean FLUID_PERMEABILITY_ENABLED = true;
    public static final List<String> FLUID_PERMEABLE_BLOCKS = ImmutableList.of(
            "#minecraft:fences"
    );
    public static final List<String> SHAPE_INDEPENDENT_FLUID_PERMEABLE_BLOCKS = ImmutableList.of(
            "#minecraft:leaves"
    );

}
