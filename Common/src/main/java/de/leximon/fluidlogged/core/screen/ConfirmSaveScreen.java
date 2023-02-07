package de.leximon.fluidlogged.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ConfirmSaveScreen extends ConfirmScreen {

    private final Screen parent;

    public ConfirmSaveScreen(Screen parent, BooleanConsumer callback) {
        super(callback, new TranslatableComponent("fluidlogged.confirm_save.title"), new TranslatableComponent("fluidlogged.confirm_save.message"));
        this.parent = parent;
    }

    @Override
    protected void addButtons(int y) {
        this.addRenderableWidget(new Button(
                this.width / 2 - 50 - 105, y,
                100, 20,
                this.yesButton, button -> this.callback.accept(true)
        ));
        this.addRenderableWidget(new Button(
                this.width / 2 - 50, y,
                100, 20,
                this.noButton, button -> this.callback.accept(false)
        ));
        this.addRenderableWidget(new Button(
                this.width / 2 - 50 + 105, y,
                100, 20,
                CommonComponents.GUI_CANCEL, button -> onClose()
        ));
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
