package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public Optional<Path> confPath() {
        ModContainer mod = FabricLoader.getInstance().getModContainer("fluidlogged").orElseThrow();
        CustomValue value = mod.getMetadata().getCustomValue("fluidlogged");
        return mod.findPath(value.getAsString());
    }
}
