package de.leximon.fluidlogged.core.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfirmSaveScreen extends ConfirmScreen {

    private final Screen parent;

    public ConfirmSaveScreen(Screen parent, BooleanConsumer callback) {
        super(callback, Text.translatable("fluidlogged.confirm_save.title"), Text.translatable("fluidlogged.confirm_save.message"));
        this.parent = parent;
    }

    @Override
    protected void addButtons(int y) {
        this.addDrawableChild(
                ButtonWidget.builder(
                                this.yesText,
                                button -> this.callback.accept(true)
                        )
                        .size(100, 20)
                        .position(this.width / 2 - 50 - 105, y)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(
                                this.noText,
                                button -> this.callback.accept(false)
                        )
                        .size(100, 20)
                        .position(this.width / 2 - 50, y)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(
                                ScreenTexts.CANCEL,
                                button -> close()
                        )
                        .size(100, 20)
                        .position(this.width / 2 - 50 + 105, y)
                        .build()
        );
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.renderBackgroundTexture(0);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
