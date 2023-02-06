package de.leximon.fluidlogged.platform.services;

import java.io.File;
import java.io.Reader;
import java.util.function.Consumer;

public interface IPlatformHelper {

    File getConfigDir();
    void loadModConfigs(Consumer<Reader> consumer);

}
