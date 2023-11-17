package de.leximon.fluidlogged.config;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.config.controller.BlockPredicateController;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

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
                                .binding(true, Config::isFluidPermeabilityEnabled, value -> Config.fluidPermeabilityEnabled = value)
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
                .category(createCategoryFrom(Config.fluidloggableBlocks))
                .category(createCategoryFrom(Config.fluidPermeableBlocks))
                .category(createCategoryFrom(Config.shapeIndependentFluidPermeableBlocks))
                .save(Config::save)
                .build()
                .generateScreen(parent);
    }

    public static ConfigCategory createCategoryFrom(BlockPredicateList list) {
        return ConfigCategory.createBuilder()
                .name(list.categoryName)
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable(list.justForFunBlacklist ? "fluidlogged.config.blacklist_just_for_fun" : "fluidlogged.config.blacklist"))
                        .description(OptionDescription.of(
                                Component.translatable("fluidlogged.config.blacklist.desc")
                        ))
                        .controller(option -> BooleanControllerBuilder.create(option)
                                .coloured(true)
                                .yesNoFormatter()
                        )
                        .binding(false, () -> list.blacklist, value -> list.blacklist = value)
                        .build()
                )
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fluidlogged.config.blocks"))
                        .description(OptionDescription.of(list.description.toArray(Component[]::new)))
                        .binding(list.defaultBlocks.get(), list::getBlocks, list::setBlocks)
                        .customController(BlockPredicateController::new)
                        .initial("")
                        .build()
                )
                .build();
    }
}
