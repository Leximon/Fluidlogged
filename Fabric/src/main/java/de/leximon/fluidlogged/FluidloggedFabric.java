package de.leximon.fluidlogged;

import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;


public class FluidloggedFabric implements ModInitializer {

	public static final boolean SODIUM_LOADED = FabricLoader.getInstance().isModLoaded("sodium");
	public static final boolean LITHIUM_LOADED = FabricLoader.getInstance().isModLoaded("lithium");
	public static final boolean MILK_LIB_LOADED = FabricLoader.getInstance().isModLoaded("milk");

	@Override
	public void onInitialize() {
		ArgumentTypeRegistry.registerArgumentType(
				Fluidlogged.id("fluid_state"),
				FluidStateArgument.class, SingletonArgumentInfo.contextAware(FluidStateArgument::fluid)
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, env) -> {
			SetFluidCommand.register(dispatcher, buildContext);
		});
	}

}
