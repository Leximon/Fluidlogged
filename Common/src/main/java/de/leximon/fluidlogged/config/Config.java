package de.leximon.fluidlogged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.api.FluidloggedRegistries;
import de.leximon.fluidlogged.config.controller.BlockPredicateController;
import de.leximon.fluidlogged.platform.services.Services;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@DefaultQualifier(NonNull.class)
public class Config {

    public static final String CONFIG_FILE_NAME = "fluidlogged.json";
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Object2BooleanMap<ResourceLocation> addons = new Object2BooleanOpenHashMap<>();

    private final BlockPredicateList fluidloggableBlocks = new BlockPredicateList(this, Addon::fluidloggableBlocks);
    private boolean fluidPermeabilityEnabled = true;
    private final BlockPredicateList fluidPermeableBlocks = new BlockPredicateList(this, Addon::fluidPermeableBlocks);
    private final BlockPredicateList shapeIndependentFluidPermeableBlocks = new BlockPredicateList(this, Addon::shapeIndependentFluidPermeableBlocks);

    public boolean isFluidloggable(BlockState block) {
        return this.fluidloggableBlocks.contains(block);
    }

    public boolean isFluidPermeable(BlockState block) {
        return this.fluidPermeableBlocks.contains(block);
    }

    public boolean isShapeIndependentFluidPermeable(BlockState block) {
        return this.shapeIndependentFluidPermeableBlocks.contains(block);
    }

    public List<Addon> getEnabledAddons() {
        List<Addon> enabledAddons = new ArrayList<>(this.addons.size());

        for (Map.Entry<ResourceLocation, Addon> addonEntry : FluidloggedRegistries.ADDONS.entrySet()) {
            ResourceLocation addonId = addonEntry.getKey();
            Addon addon = addonEntry.getValue();

            boolean enabled = this.addons.getBoolean(addonId) || (!this.addons.containsKey(addonId) && addon.enabledByDefault());
            if (!enabled)
                continue;

            enabledAddons.add(addon);
        }

        return enabledAddons;
    }

    public void compile() {
        this.fluidloggableBlocks.compile();
        this.fluidPermeableBlocks.compile();
        this.shapeIndependentFluidPermeableBlocks.compile();
    }

    public void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        JsonObject addonsObj = new JsonObject();
        for (Object2BooleanMap.Entry<ResourceLocation> entry : this.addons.object2BooleanEntrySet()) {
            String id = entry.getKey().toString();
            boolean enabled = entry.getBooleanValue();
            addonsObj.addProperty(id, enabled);
        }
        obj.add("addons", addonsObj);

        obj.addProperty("fluid_permeability_enabled", this.fluidPermeabilityEnabled);
        obj.add("fluidloggable_blocks", this.fluidloggableBlocks.toJson());
        obj.add("fluid_permeable_blocks", this.fluidPermeableBlocks.toJson());
        obj.add("shape_independent_fluid_permeable_blocks", this.shapeIndependentFluidPermeableBlocks.toJson());

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config " + CONFIG_FILE_NAME, e);
        }
    }

    public void load() {
        File file = Services.PLATFORM.getConfigFile();
        if (!file.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            if (obj.has("addons")) {
                JsonObject addonsObj = obj.getAsJsonObject("addons");
                for (String id : addonsObj.keySet()) {
                    ResourceLocation addonId = new ResourceLocation(id);
                    boolean enabled = addonsObj.get(id).getAsBoolean();
                    this.addons.put(addonId, enabled);
                }
            }

            if (obj.has("fluid_permeability_enabled"))
                this.fluidPermeabilityEnabled = obj.get("fluid_permeability_enabled").getAsBoolean();

            if (obj.has("fluidloggable_blocks") && obj.get("fluidloggable_blocks").isJsonObject())
                this.fluidloggableBlocks.fromJson(obj.getAsJsonObject("fluidloggable_blocks"));

            if (obj.has("fluid_permeable_blocks"))
                this.fluidPermeableBlocks.fromJson(obj.getAsJsonObject("fluid_permeable_blocks"));

            if (obj.has("shape_independent_fluid_permeable_blocks"))
                this.shapeIndependentFluidPermeableBlocks.fromJson(obj.getAsJsonObject("shape_independent_fluid_permeable_blocks"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config " + CONFIG_FILE_NAME, e);
        }

    }

    public Screen createConfigScreen(Screen parent) {
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
                                .binding(true, () -> this.fluidPermeabilityEnabled, value -> this.fluidPermeabilityEnabled = value)
                                .build()
                        )
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Quick Presets"))
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Redstone Components"))
                                        .text(Component.literal("add/remove"))
                                        .action((screen, option) -> {

                                        })
                                        .build()
                                )
                                .build())
                        .build()
                )
                .category(createBlockListCategory(
                        this.fluidloggableBlocks,
                        false,
                        Component.translatable("fluidlogged.config.fluidloggable_blocks"),
                        Component.translatable("fluidlogged.config.fluidloggable_blocks.desc")
                ))
                .category(createBlockListCategory(
                        this.fluidPermeableBlocks,
                        false,
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks"),
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc"),
                        Component.empty(),
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc.note")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                ))
                .category(createBlockListCategory(
                        this.shapeIndependentFluidPermeableBlocks,
                        true,
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks"),
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc"),
                        Component.empty(),
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc.note")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                ))
                .save(this::save)
                .build()
                .generateScreen(parent);
    }

    private ConfigCategory createBlockListCategory(BlockPredicateList list, boolean justForFunBlackList, Component name, Component... description) {
        return ConfigCategory.createBuilder()
                .name(name)
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable(justForFunBlackList ? "fluidlogged.config.blacklist_just_for_fun" : "fluidlogged.config.blacklist"))
                        .description(OptionDescription.of(
                                Component.translatable("fluidlogged.config.blacklist.desc")
                        ))
                        .controller(option -> BooleanControllerBuilder.create(option)
                                .coloured(true)
                                .yesNoFormatter()
                        )
                        .binding(false, list::isBlacklist, list::setBlacklist)
                        .build()
                )
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fluidlogged.config.blocks"))
                        .description(OptionDescription.of(description))
                        .binding(Collections.emptyList(), list::getEntries, list::setEntries)
                        .customController(BlockPredicateController::new)
                        .initial("")
                        .build()
                )
                .build();
    }
}
