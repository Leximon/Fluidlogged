package de.leximon.fluidlogged.core.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

public class ConfirmSaveScreen extends ConfirmScreen {

    private final Screen parent;

    public ConfirmSaveScreen(Screen parent, BooleanConsumer callback) {
        super(callback, new TranslatableText("fluidlogged.confirm_save.title"), new TranslatableText("fluidlogged.confirm_save.message"));
        this.parent = parent;
    }

    @Override
    protected void addButtons(int y) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50 - 105, y, 100, 20, this.yesTranslated, button -> this.callback.accept(true)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, y, 100, 20, this.noTranslated, button -> this.callback.accept(false)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50 + 105, y, 100, 20, ScreenTexts.CANCEL, button -> close()));
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
