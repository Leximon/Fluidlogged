package de.leximon.fluidlogged;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.config.YaclMissingScreen;
import net.fabricmc.loader.api.FabricLoader;

public class FluidloggedFabricModMenu implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if(!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) // we could cache this value but it's not worth either
            return YaclMissingScreen::new;
        return Config::createConfigScreen;
    }
}
