package de.leximon.fluidlogged.config;

import java.util.List;

public interface ConfigDefaults {

    List<String> fluidloggableBlocks();

    List<String> fluidPermeableBlocks();

    List<String> shapeIndependentFluidPermeableBlocks();
}
