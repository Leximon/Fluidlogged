package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public void loadModConfigs(Consumer<Reader> consumer) {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            CustomValue value = mod.getMetadata().getCustomValue("fluidlogged");
            if(value == null)
                continue;
            Optional<Path> confPath = mod.findPath(value.getAsString());
            if(confPath.isPresent()) {
                try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(confPath.get()))) {
                    consumer.accept(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
