package de.leximon.fluidlogged.config;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.config.controller.BlockPredicateController;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;

@ApiStatus.Internal
public class ConfigScreen {
    public static Screen create(Screen parent) {
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
                                .binding(true, () -> Fluidlogged.CONFIG.fluidPermeabilityEnabled, value -> Fluidlogged.CONFIG.fluidPermeabilityEnabled = value)
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
                        Fluidlogged.CONFIG.fluidloggableBlocks,
                        false,
                        Component.translatable("fluidlogged.config.fluidloggable_blocks"),
                        Component.translatable("fluidlogged.config.fluidloggable_blocks.desc")
                ))
                .category(createBlockListCategory(
                        Fluidlogged.CONFIG.fluidPermeableBlocks,
                        false,
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks"),
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc"),
                        Component.empty(),
                        Component.translatable("fluidlogged.config.fluid_permeable_blocks.desc.note")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                ))
                .category(createBlockListCategory(
                        Fluidlogged.CONFIG.shapeIndependentFluidPermeableBlocks,
                        true,
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks"),
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc"),
                        Component.empty(),
                        Component.translatable("fluidlogged.config.shape_independent_fluid_permeable_blocks.desc.note")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                ))
                .save(Fluidlogged.CONFIG::save)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory createBlockListCategory(BlockPredicateList list, boolean justForFunBlackList, Component name, Component... description) {
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
