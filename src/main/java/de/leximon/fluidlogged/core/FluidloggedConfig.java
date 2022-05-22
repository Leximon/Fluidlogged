package de.leximon.fluidlogged.core;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeConfigSpec;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FluidloggedConfig
{
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> fluids;
    public static ForgeConfigSpec.ConfigValue<Boolean> compatibilityMode;

    //I need these to load the config before mod initialization.
    public static final String path = FMLPaths.CONFIGDIR.get().toString()+"\\fluidlogged-common.toml";
    public static Config config;
    public static boolean exists = false;

    public FluidloggedConfig(ForgeConfigSpec.Builder builder)
    {
        compatibilityMode = builder.comment("By default, this mod will work with all waterloggable blocks. However, some mods add these blocks in a way that will cause a crash. \n " +
                "If compatibility mode is true, this mod will only apply to vanilla waterloggable block types(but will still work on modded blocks of those types).").define("compatibilityMode", false);
        fluids = builder.comment("Adds fluids to the list. Ex: [\"minecraft:lava\", \"create:honey\"]").defineList("fluids", Lists.newArrayList(Fluids.LAVA.getRegistryName().toString()), Objects::nonNull);
    }
    public static final FluidloggedConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static
    {
        FluidloggedConfig.readConfig();

        final Pair<FluidloggedConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(FluidloggedConfig::new);
        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static void readConfig()
    {
        exists = new File(path).exists();
        if(exists)
        {
            TomlParser parser = TomlFormat.instance().createParser();
            config = parser.parse(Paths.get(path), new Action());
        }
    }

    public static List<? extends String> getFluidList()
    {
        if(exists)
        {
            return config.getRaw(fluids.getPath());
        }
        return fluids.get();
    }

    public static boolean getCompatibilityMode()
    {
        if(exists)
        {
            return config.getRaw(compatibilityMode.getPath());
        }
        return false;
    }

    static class Action implements FileNotFoundAction
    {

        @Override
        public boolean run(Path file, ConfigFormat<?> configFormat) throws IOException
        {
            return (new File(path).exists());
        }
    }
}
