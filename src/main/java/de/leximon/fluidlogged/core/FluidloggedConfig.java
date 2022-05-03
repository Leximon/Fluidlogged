package de.leximon.fluidlogged.core;

import eu.midnightdust.lib.config.MidnightConfig;

import java.util.ArrayList;
import java.util.List;

public class FluidloggedConfig {

    @MidnightConfig.Comment public static MidnightConfig.Comment restart;
    @MidnightConfig.Entry public static boolean printFluidIds = false;
    @MidnightConfig.Entry public static List<String> fluids = new ArrayList<>();

    static {
        fluids.add("minecraft:lava");
    }

}
