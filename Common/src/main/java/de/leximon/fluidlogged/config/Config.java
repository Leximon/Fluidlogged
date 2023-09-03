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

    private static boolean fluidPassageEnabled = ConfigDefaults.FLUID_PASSAGE_ENABLED;
    private static List<String> fluidpassableBlocks = ConfigDefaults.FLUIDPASSABLE_BLOCKS;
    private static List<BlockPredicateEntry> fluidpassableBlockPredicates = ImmutableList.of();


    public static void setFluidloggableBlocks(List<String> blocks) {
        fluidloggableBlocks = blocks;
        fluidloggableBlockPredicates = blocks.stream()
                .map(BlockPredicateEntry::ofBlockOrTag)
                .filter(Objects::nonNull)
                .toList();
    }

    public static void setFluidPassageEnabled(boolean fluidPassageEnabled) {
        Config.fluidPassageEnabled = fluidPassageEnabled;
    }

    public static void setFluidpassableBlocks(List<String> blocks) {
        fluidpassableBlocks = blocks;
        fluidpassableBlockPredicates = blocks.stream()
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

    public static boolean isFluidPassageEnabled() {
        return fluidPassageEnabled;
    }

    public static boolean isFluidpassable(BlockState block) {
        for (BlockPredicateEntry predicate : fluidpassableBlockPredicates)
            if (predicate.check(block))
                return true;
        return false;
    }


    public static void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        obj.add("fluidloggable_blocks", GSON.toJsonTree(fluidloggableBlocks));

        JsonObject fluidPassage = new JsonObject();
        fluidPassage.addProperty("enabled", fluidPassageEnabled);
        fluidPassage.add("fluidpassable_blocks", GSON.toJsonTree(fluidpassableBlocks));
        obj.add("fluid_passage", fluidPassage);

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

            if (obj.has("fluid_passage")) {
                JsonObject fluidPassage = obj.getAsJsonObject("fluid_passage");

                if (fluidPassage.has("enabled"))
                    setFluidPassageEnabled(fluidPassage.get("enabled").getAsBoolean());

                if (fluidPassage.has("fluidpassable_blocks")) {
                    JsonElement element = fluidPassage.get("fluidpassable_blocks");
                    List<String> fluidpassableBlocks = GSON.fromJson(element, new TypeToken<>() {});
                    setFluidpassableBlocks(fluidpassableBlocks);
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
                        .name(Component.translatable("fluidlogged.config.fluid_passage"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_passage.enabled"))
                                .controller(BooleanControllerBuilder::create)
                                .binding(ConfigDefaults.FLUID_PASSAGE_ENABLED, () -> fluidPassageEnabled, Config::setFluidPassageEnabled)
                                .build()
                        )
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("fluidlogged.config.fluid_passage.fluidpassable_blocks"))
                                .binding(ConfigDefaults.FLUIDPASSABLE_BLOCKS, () -> fluidpassableBlocks, Config::setFluidpassableBlocks)
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
