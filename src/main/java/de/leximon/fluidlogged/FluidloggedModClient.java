package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static de.leximon.fluidlogged.FluidloggedMod.LOGGER;

public class FluidloggedModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(c -> {
            if (FluidloggedConfig.printFluidIds) {
                LOGGER.info("----- FLUID IDS -----");
                for (ResourceLocation key : Registry.FLUID.keySet())
                    LOGGER.info(key.toString());
                LOGGER.info("---------------------");
            }
        });
    }
}
