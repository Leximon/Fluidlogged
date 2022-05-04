package de.leximon.fluidlogged.core;

import de.leximon.fluidlogged.FluidloggedMod;
import eu.midnightdust.lib.config.MidnightConfig;

import java.util.ArrayList;
import java.util.List;

public class FluidloggedConfig {

    @MidnightConfig.Comment public static MidnightConfig.Comment restart;
    @MidnightConfig.Entry public static boolean compatibilityMode = false;
    @MidnightConfig.Entry public static List<String> fluids = new ArrayList<>();
    @MidnightConfig.Entry public static boolean printFluidIds = false;

    static {
        fluids.add("minecraft:lava");
        MidnightConfig.init(FluidloggedMod.MOD_ID, FluidloggedConfig.class);
    }

}
