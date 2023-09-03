package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.leximon.fluidlogged.platform.services.Services;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class Config {

    public static final String CONFIG_FILE_NAME = "fluidlogged.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static List<String> fluidloggableBlocks = ConfigDefaults.FLUIDLOGGABLE_BLOCKS;
    private static List<BlockPredicateEntry> fluidloggableBlockPredicates = ImmutableList.of();

    private static boolean fluidPermeabilityEnabled = ConfigDefaults.FLUID_PASSAGE_ENABLED;
    private static List<String> fluidPermeableBlocks = ConfigDefaults.FLUIDPASSABLE_BLOCKS;
    private static List<BlockPredicateEntry> fluidPermeableBlockPredicates = ImmutableList.of();


    public static void setFluidloggableBlocks(List<String> blocks) {
        fluidloggableBlocks = blocks;
        fluidloggableBlockPredicates = blocks.stream()
                .map(BlockPredicateEntry::ofBlockOrTag)
                .filter(Objects::nonNull)
                .toList();
    }

    public static void setFluidPermeabilityEnabled(boolean fluidPermeabilityEnabled) {
        Config.fluidPermeabilityEnabled = fluidPermeabilityEnabled;
    }

    public static void setFluidPermeableBlocks(List<String> blocks) {
        fluidPermeableBlocks = blocks;
        fluidPermeableBlockPredicates = blocks.stream()
                .map(BlockPredicateEntry::ofBlockOrTag)
                .filter(Objects::nonNull)
                .toList();
    }


    public static boolean isFluidloggable(BlockState block) {
        for (BlockPredicateEntry predicate : fluidloggableBlockPredicates)
            if (predicate.check(block))
                return true;
        return false;
    }

    public static boolean isFluidPermeabilityEnabled() {
        return fluidPermeabilityEnabled;
    }

    public static boolean isFluidPermeable(BlockState block) {
        for (BlockPredicateEntry predicate : fluidPermeableBlockPredicates)
            if (predicate.check(block))
                return true;
        return false;
    }


    public static void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        obj.add("fluidloggable_blocks", GSON.toJsonTree(fluidloggableBlocks));

        JsonObject fluidPassage = new JsonObject();
        fluidPassage.addProperty("enabled", fluidPermeabilityEnabled);
        fluidPassage.add("fluid_permeable_blocks", GSON.toJsonTree(fluidPermeableBlocks));
        obj.add("fluid_permeability", fluidPassage);

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config " + CONFIG_FILE_NAME, e);
        }
    }

    public static void load() {
        File file = Services.PLATFORM.getConfigFile();
        if (!file.exists())
            return;

        try (FileReader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            if (obj.has("fluidloggable_blocks")) {
                JsonElement element = obj.get("fluidloggable_blocks");
                List<String> fluidloggableBlocks = GSON.fromJson(element, new TypeToken<>() {});
                setFluidloggableBlocks(fluidloggableBlocks);
            }

            if (obj.has("fluid_permeability")) {
                JsonObject fluidPassage = obj.getAsJsonObject("fluid_permeability");

                if (fluidPassage.has("enabled"))
                    setFluidPermeabilityEnabled(fluidPassage.get("enabled").getAsBoolean());

                if (fluidPassage.has("fluid_permeable_blocks")) {
                    JsonElement element = fluidPassage.get("fluid_permeable_blocks");
                    List<String> fluidpassableBlocks = GSON.fromJson(element, new TypeToken<>() {});
                    setFluidPermeableBlocks(fluidpassableBlocks);
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
                                .binding(ConfigDefaults.FLUIDLOGGABLE_BLOCKS, () -> fluidloggableBlocks, Config::setFluidloggableBlocks)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build()
                        )
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("fluidlogged.config.fluid_permeability"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_permeability.enabled"))
                                .controller(o -> BooleanControllerBuilder.create(o)
                                        .coloured(true)
                                        .yesNoFormatter()
                                )
                                .binding(ConfigDefaults.FLUID_PASSAGE_ENABLED, () -> fluidPermeabilityEnabled, Config::setFluidPermeabilityEnabled)
                                .build()
                        )
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_permeability.fluid_permeable_blocks"))
                                .binding(ConfigDefaults.FLUIDPASSABLE_BLOCKS, () -> fluidPermeableBlocks, Config::setFluidPermeableBlocks)
                                .controller(StringControllerBuilder::create)
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
