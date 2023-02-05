package de.leximon.fluidlogged.platform.services;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface IPlatformHelper {

    File getConfigDir();
    Optional<Path> confPath();

}
