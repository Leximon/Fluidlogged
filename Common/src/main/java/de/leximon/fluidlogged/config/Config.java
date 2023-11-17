package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static final String CONFIG_FILE_NAME = "fluidlogged.json";
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final ConfigDefaults CONFIG_DEFAULTS = Services.PLATFORM.getConfigDefaults();

    static final BlockPredicateList fluidloggableBlocks = new BlockPredicateList(
            CONFIG_DEFAULTS::fluidloggableBlocks, false,
            Component.translatable("fluidlogged.config.fluidloggable_blocks"),
            ImmutableList.of(
                    Component.translatable("fluidlogged.config.fluidloggable_blocks.desc")
            )
    );

    static boolean fluidPermeabilityEnabled = true;
    static final BlockPredicateList fluidPermeableBlocks = new BlockPredicateList(
            CONFIG_DEFAULTS::fluidPermeableBlocks, false,
            Component.translatable("fluidlogged.config.fluid_permeable_blocks"),
            ImmutableList.of(
                    Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc"),
                    Component.empty(),
                    Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc.note")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            )
    );
    static final BlockPredicateList shapeIndependentFluidPermeableBlocks = new BlockPredicateList(
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
}
