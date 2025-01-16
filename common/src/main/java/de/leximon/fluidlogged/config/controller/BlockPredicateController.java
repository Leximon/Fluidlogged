package de.leximon.fluidlogged.config.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringController;

public class BlockPredicateController extends StringController {

    public BlockPredicateController(Option<String> option) {
        super(option);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new BlockPredicateControllerElement(this, screen, widgetDimension, true);
    }
}
