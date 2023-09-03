package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ConfigDefaults {

    public static final List<String> FLUIDLOGGABLE_BLOCKS = ImmutableList.of(
            "#minecraft:pressure_plates",
            "#minecraft:doors"
    );

    public static final boolean FLUID_PASSAGE_ENABLED = true;
    public static final List<String> FLUIDPASSABLE_BLOCKS = ImmutableList.of(
            "#minecraft:fences"
    );

}
