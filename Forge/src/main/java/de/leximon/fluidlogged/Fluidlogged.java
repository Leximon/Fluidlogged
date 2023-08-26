package de.leximon.fluidlogged;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("fluidlogged")
public class Fluidlogged {

    public Fluidlogged() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClient);
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(){
        // Register the configuration GUI factory
//        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, returnTo) ->  new ConfigScreen(returnTo)));
    }

}
