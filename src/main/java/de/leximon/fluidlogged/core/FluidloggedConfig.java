package de.leximon.fluidlogged.core;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeConfigSpec;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

public class FluidloggedConfig
{
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fluids;
    public static ForgeConfigSpec.ConfigValue<Boolean> compatibilityMode;
    public static ForgeConfigSpec.ConfigValue<Boolean> printFluidIds;

    public FluidloggedConfig(ForgeConfigSpec.Builder builder)
    {
        compatibilityMode = builder.comment("By default, this mod will work with all waterloggable blocks. However, some mods add these blocks in a way that will cause a crash. \n " +
                "If compatibility mode is true, this mod will only apply to vanilla Waterloggable block types(but will still work on modded blocks of those types.).").define("compatibilityMode", false);
        fluids = builder.comment("Adds fluids to the list. Ex: [\"minecraft:lava\", \"create:honey\"]").defineList("fluids", Lists.newArrayList(Fluids.LAVA.getRegistryName().toString()), Objects::nonNull);
        printFluidIds = builder.comment("If set to true, will print all fluid IDs in the log on game initialization.").define("printFluidIds", false);
    }
    public static final FluidloggedConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;
    static
    {
        final Pair<FluidloggedConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(FluidloggedConfig::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static List<? extends String> getFluidList() {
        Fluidlogged.LOGGER.info("Recieving fluids from config... " + fluids.get());
        return fluids.get();
    }
}
