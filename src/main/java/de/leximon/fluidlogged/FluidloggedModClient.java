package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static de.leximon.fluidlogged.FluidloggedMod.LOGGER;
import static de.leximon.fluidlogged.FluidloggedMod.initFluidBuckets;

public class FluidloggedModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(c -> {
            initFluidBuckets();

            if (FluidloggedConfig.printFluidIds) {
                LOGGER.info("----- FLUID IDS -----");
                for (Identifier id : Registry.FLUID.getIds())
                    LOGGER.info(id.toString());
                LOGGER.info("---------------------");
            }
        });
    }
}
