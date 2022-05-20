package darth.fluidlogged;

import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeConfigSpec;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Config
{
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fluids;

    public Config(ForgeConfigSpec.Builder builder)
    {
        fluids = builder.comment(" ").defineList("fluids", Lists.newArrayList(Fluids.LAVA.getRegistryName().toString()), String.class::isInstance);
    }
    public static final Config CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;
    static
    {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }
}
