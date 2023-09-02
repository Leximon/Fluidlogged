package de.leximon.fluidlogged;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.leximon.fluidlogged.config.Config;

public class FluidloggedFabricModMenu implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Config::createConfigScreen;
    }

}
