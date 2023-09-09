package de.leximon.fluidlogged.config;

import java.util.List;

public interface ConfigDefaults {

    List<String> fluidloggableBlocks();

    boolean fluidPermeabilityEnabled();

    List<String> fluidPermeableBlocks();

    List<String> shapeIndependentFluidPermeableBlocks();
}
