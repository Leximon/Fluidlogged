package de.leximon.fluidlogged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.config.controller.BlockPredicateController;
import de.leximon.fluidlogged.platform.services.Services;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.io.*;
import java.util.List;

public class Config {

    public static final String CONFIG_FILE_NAME = "fluidlogged.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final BlockPredicateList fluidloggableBlocks = new BlockPredicateList(ConfigDefaults.FLUIDLOGGABLE_BLOCKS);

    private static boolean fluidPermeabilityEnabled = ConfigDefaults.FLUID_PERMEABILITY_ENABLED;
    private static final BlockPredicateList fluidPermeableBlocks = new BlockPredicateList(ConfigDefaults.FLUID_PERMEABLE_BLOCKS);
    private static final BlockPredicateList shapeIndependentFluidPermeableBlocks = new BlockPredicateList(ConfigDefaults.SHAPE_INDEPENDENT_FLUID_PERMEABLE_BLOCKS);



    public static boolean isFluidloggable(BlockState block) {
        return fluidloggableBlocks.contains(block);
    }

    public static boolean isFluidPermeabilityEnabled() {
        return fluidPermeabilityEnabled;
    }

    public static boolean isFluidPermeable(BlockState block) {
        return fluidPermeableBlocks.contains(block);
    }

    public static boolean isShapeIndependentFluidPermeable(BlockState block) {
        return shapeIndependentFluidPermeableBlocks.contains(block);
    }

    public static void invalidateCaches() {
        fluidloggableBlocks.invalidateCache();
        fluidPermeableBlocks.invalidateCache();
        shapeIndependentFluidPermeableBlocks.invalidateCache();
    }

    public static void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        obj.add("fluidloggable_blocks", GSON.toJsonTree(fluidloggableBlocks.getBlocks()));

        JsonObject fluidPassage = new JsonObject();
        fluidPassage.addProperty("enabled", fluidPermeabilityEnabled);
        fluidPassage.add("fluid_permeable_blocks", GSON.toJsonTree(fluidPermeableBlocks.getBlocks()));
        fluidPassage.add("shape_independent_fluid_permeable_blocks", GSON.toJsonTree(shapeIndependentFluidPermeableBlocks.getBlocks()));
        obj.add("fluid_permeability", fluidPassage);

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config " + CONFIG_FILE_NAME, e);
        }
    }

    public static void load() {
        File file = Services.PLATFORM.getConfigFile();
        if (!file.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            if (obj.has("fluidloggable_blocks")) {
                JsonElement element = obj.get("fluidloggable_blocks");
                List<String> blocks = GSON.fromJson(element, new TypeToken<>() {});
                fluidloggableBlocks.setBlocks(blocks);
            }

            if (obj.has("fluid_permeability")) {
                JsonObject fluidPassage = obj.getAsJsonObject("fluid_permeability");

                if (fluidPassage.has("enabled"))
                    fluidPermeabilityEnabled = fluidPassage.get("enabled").getAsBoolean();

                if (fluidPassage.has("fluid_permeable_blocks")) {
                    JsonElement element = fluidPassage.get("fluid_permeable_blocks");
                    List<String> blocks = GSON.fromJson(element, new TypeToken<>() {});
                    fluidPermeableBlocks.setBlocks(blocks);
                }

                if (fluidPassage.has("shape_independent_fluid_permeable_blocks")) {
                    JsonElement element = fluidPassage.get("shape_independent_fluid_permeable_blocks");
                    List<String> blocks = GSON.fromJson(element, new TypeToken<>() {});
                    shapeIndependentFluidPermeableBlocks.setBlocks(blocks);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config " + CONFIG_FILE_NAME, e);
        }

    }

    public static Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("fluidlogged.config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fluidlogged.config.general"))
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("fluidlogged.config.general.fluidloggable_blocks"))
                                .description(OptionDescription.of(
                                        Component.translatable("fluidlogged.config.general.fluidloggable_blocks.desc")
                                ))
                                .binding(ConfigDefaults.FLUIDLOGGABLE_BLOCKS, fluidloggableBlocks::getBlocks, fluidloggableBlocks::setBlocks)
                                .customController(BlockPredicateController::new)
                                .initial("")
                                .build()
                        )
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fluidlogged.config.fluid_permeability"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_permeability.enabled"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("fluidlogged.config.fluid_permeability.enabled.desc"))
                                        .image(Fluidlogged.id("textures/fluid_permeability_example.png"), 520, 293)
                                        .build()
                                )
                                .controller(option -> BooleanControllerBuilder.create(option)
                                        .coloured(true)
                                        .yesNoFormatter()
                                )
                                .binding(ConfigDefaults.FLUID_PERMEABILITY_ENABLED, () -> fluidPermeabilityEnabled, value -> fluidPermeabilityEnabled = value)
                                .build()
                        )
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_permeability.fluid_permeable_blocks"))
                                .description(OptionDescription.of(
                                        Component.translatable("fluidlogged.config.fluid_permeability.fluid_permeable_blocks.desc"),
                                        Component.empty(),
                                        Component.translatable("fluidlogged.config.fluid_permeability.fluid_permeable_blocks.desc.note")
                                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                                ))
                                .binding(ConfigDefaults.FLUID_PERMEABLE_BLOCKS, fluidPermeableBlocks::getBlocks, fluidPermeableBlocks::setBlocks)
                                .customController(BlockPredicateController::new)
                                .initial("")
                                .build()
                        )
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_permeability.shape_independent_fluid_permeable_blocks"))
                                .description(OptionDescription.of(
                                        Component.translatable("fluidlogged.config.fluid_permeability.shape_independent_fluid_permeable_blocks.desc"),
                                        Component.empty(),
                                        Component.translatable("fluidlogged.config.fluid_permeability.shape_independent_fluid_permeable_blocks.desc.note")
                                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                                ))
                                .binding(ConfigDefaults.FLUID_PERMEABLE_BLOCKS, shapeIndependentFluidPermeableBlocks::getBlocks, shapeIndependentFluidPermeableBlocks::setBlocks)
                                .customController(BlockPredicateController::new)
                                .initial("")
                                .build()
                        )
                        .build()
                )
                .save(Config::save)
                .build()
                .generateScreen(parent);
    }
}
