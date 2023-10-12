package de.leximon.fluidlogged;

import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.content.EmbeddedBlockEntity;
import de.leximon.fluidlogged.content.EmbeddedBlockModel;
import de.leximon.fluidlogged.content.EmbeddedObsidian;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class FluidloggedFabric implements ModInitializer {

	public static Block BLOCK_COATED_OBSIDIAN;
	public static BlockEntityType<EmbeddedBlockEntity> BLOCK_ENTITY_COATED_BLOCK;

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

		ServerLifecycleEvents.SERVER_STARTED.register(server -> Config.compile());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			if (success)
				Config.compile();
		});

		BLOCK_COATED_OBSIDIAN = Registry.register(
				BuiltInRegistries.BLOCK, Fluidlogged.id("embedded_obsidian"),
				new EmbeddedObsidian(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN))
		);

		BLOCK_ENTITY_COATED_BLOCK = Registry.register(
				BuiltInRegistries.BLOCK_ENTITY_TYPE, Fluidlogged.id("embedded_block"),
				FabricBlockEntityTypeBuilder.create(EmbeddedBlockEntity::new, BLOCK_COATED_OBSIDIAN).build()
		);

		ModelLoadingPlugin.register(pluginContext -> {

            pluginContext.modifyModelAfterBake().register((model, context) -> {
				ResourceLocation id = context.id();
				if (id.getNamespace().equals(Fluidlogged.MOD_ID) && id.getPath().equals("embedded_obsidian"))
					return new EmbeddedBlockModel(model);
				return model;
			});
        });
	}

}
