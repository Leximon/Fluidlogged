package de.leximon.fluidlogged;

import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;


public class FluidloggedFabric implements ModInitializer {

	@Override
	public void onInitialize() {

		Fluidlogged.Internal.initialize();

		ArgumentTypeRegistry.registerArgumentType(
				Fluidlogged.id("fluid_state"),
				FluidStateArgument.class, SingletonArgumentInfo.contextAware(FluidStateArgument::fluid)
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, env) -> {
			SetFluidCommand.register(dispatcher, buildContext);
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> Fluidlogged.CONFIG.compile());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			if (success)
				Fluidlogged.CONFIG.compile();
		});
	}

}
