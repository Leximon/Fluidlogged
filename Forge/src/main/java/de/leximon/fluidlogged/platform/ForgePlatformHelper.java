package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.platform.services.IPlatformHelper;

import java.io.*;
import java.util.function.Consumer;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public File getConfigDir() {
        return new File("config");
    }

    @Override
    public void loadModConfigs(Consumer<Reader> consumer) {
        InputStream in = getClass().getClassLoader().getResourceAsStream("fluidlogged.mod.json");
        try (Reader reader = new InputStreamReader(in, "UTF-8")){
            consumer.accept(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
