package de.leximon.fluidlogged.core;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeConfigSpec;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

public class Config
{
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fluids;
    public static ForgeConfigSpec.ConfigValue<Boolean> compatibilityMode;
    public static ForgeConfigSpec.ConfigValue<Boolean> printFluidIds;

    public Config(ForgeConfigSpec.Builder builder)
    {
        compatibilityMode = builder.comment(" ").define("compatibilityMode", false);
        fluids = builder.comment(" ").defineList("fluids", Lists.newArrayList(Fluids.LAVA.getRegistryName().toString()), Objects::nonNull);
        printFluidIds = builder.comment(" ").define("printFluidIds", false);
    }
    public static final Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;
    static
    {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static List<? extends String> getFluidList() {
        return fluids.get();
    }
}
