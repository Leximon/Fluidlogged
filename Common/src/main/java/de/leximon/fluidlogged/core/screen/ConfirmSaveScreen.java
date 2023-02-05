package de.leximon.fluidlogged.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfirmSaveScreen extends ConfirmScreen {

    private final Screen parent;

    public ConfirmSaveScreen(Screen parent, BooleanConsumer callback) {
        super(callback, Component.translatable("fluidlogged.confirm_save.title"), Component.translatable("fluidlogged.confirm_save.message"));
        this.parent = parent;
    }

    @Override
    protected void addButtons(int y) {
        this.addRenderableWidget(
                Button.builder(
                                this.yesButton,
                                button -> this.callback.accept(true)
                        )
                        .size(100, 20)
                        .pos(this.width / 2 - 50 - 105, y)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(
                                this.noButton,
                                button -> this.callback.accept(false)
                        )
                        .size(100, 20)
                        .pos(this.width / 2 - 50, y)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(
                                CommonComponents.GUI_CANCEL,
                                button -> onClose()
                        )
                        .size(100, 20)
                        .pos(this.width / 2 - 50 + 105, y)
                        .build()
        );
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        this.renderDirtBackground(0);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
