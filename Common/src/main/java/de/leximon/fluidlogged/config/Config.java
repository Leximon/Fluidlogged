package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.leximon.fluidlogged.Fluidlogged;
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
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final ConfigDefaults CONFIG_DEFAULTS = Services.PLATFORM.getConfigDefaults();

    private static final BlockPredicateList fluidloggableBlocks = new BlockPredicateList(
            CONFIG_DEFAULTS::fluidloggableBlocks, false,
            Component.translatable("fluidlogged.config.fluidloggable_blocks"),
            ImmutableList.of(
                    Component.translatable("fluidlogged.config.fluidloggable_blocks.desc")
            )
    );

    private static boolean fluidPermeabilityEnabled = true;
    private static final BlockPredicateList fluidPermeableBlocks = new BlockPredicateList(
            CONFIG_DEFAULTS::fluidPermeableBlocks, false,
            Component.translatable("fluidlogged.config.fluid_permeable_blocks"),
            ImmutableList.of(
                    Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc"),
                    Component.empty(),
                    Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc.note")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            )
    );
    private static final BlockPredicateList shapeIndependentFluidPermeableBlocks = new BlockPredicateList(
            CONFIG_DEFAULTS::shapeIndependentFluidPermeableBlocks, true,
            Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks"),
            ImmutableList.of(
                    Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc"),
                    Component.empty(),
                    Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc.note")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            )
    );



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

    public static void compile() {
        fluidloggableBlocks.compile();
        fluidPermeableBlocks.compile();
        shapeIndependentFluidPermeableBlocks.compile();
    }

    public static void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        obj.addProperty("fluid_permeability_enabled", fluidPermeabilityEnabled);
        obj.add("fluidloggable_blocks", fluidloggableBlocks.toJson());
        obj.add("fluid_permeable_blocks", fluidPermeableBlocks.toJson());
        obj.add("shape_independent_fluid_permeable_blocks", shapeIndependentFluidPermeableBlocks.toJson());

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

            if (obj.has("fluid_permeability_enabled"))
                fluidPermeabilityEnabled = obj.get("fluid_permeability_enabled").getAsBoolean();

            if (obj.has("fluidloggable_blocks") && obj.get("fluidloggable_blocks").isJsonObject())
                fluidloggableBlocks.fromJson(obj.getAsJsonObject("fluidloggable_blocks"));

            if (obj.has("fluid_permeable_blocks"))
                fluidPermeableBlocks.fromJson(obj.getAsJsonObject("fluid_permeable_blocks"));

            if (obj.has("shape_independent_fluid_permeable_blocks"))
                shapeIndependentFluidPermeableBlocks.fromJson(obj.getAsJsonObject("shape_independent_fluid_permeable_blocks"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config " + CONFIG_FILE_NAME, e);
        }

    }

    public static Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("fluidlogged.config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fluidlogged.config.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fluidlogged.config.general.enable_fluid_permeability"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("fluidlogged.config.general.enable_fluid_permeability.desc"))
                                        .image(Fluidlogged.id("textures/fluid_permeability_example.png"), 520, 293)
                                        .build()
                                )
                                .controller(option -> BooleanControllerBuilder.create(option)
                                        .coloured(true)
                                        .yesNoFormatter()
                                )
                                .binding(true, () -> fluidPermeabilityEnabled, value -> fluidPermeabilityEnabled = value)
                                .build()
                        )
                        .build()
                )
                .category(fluidloggableBlocks.createCategory())
                .category(fluidPermeableBlocks.createCategory())
                .category(shapeIndependentFluidPermeableBlocks.createCategory())
                .save(Config::save)
                .build()
                .generateScreen(parent);
    }
}
