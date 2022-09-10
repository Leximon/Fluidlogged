package de.leximon.fluidlogged.core.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class FluidConfigScreen extends Screen {

    protected final ConfigScreen parent;

    private FluidListWidget fluidList;

    protected FluidConfigScreen(ConfigScreen parent) {
        super(new TranslatableText("fluidlogged.fluid_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if(fluidList == null)
            fluidList = new FluidListWidget(this, client);
        else
            fluidList.updateSize();
        addSelectableChild(fluidList);
        addDrawableChild(new ButtonWidget(this.width / 2 - 75, this.height - 29, 150, 20, ScreenTexts.DONE, button -> close()));
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.renderBackgroundTexture(0);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.fluidList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 12, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }


    @Override
    public void close() {
        boolean anyEntryEnabled = false;
        for (FluidListWidget.Entry entry : fluidList.children())
            if (entry instanceof FluidListWidget.FluidEntry fluidEntry && fluidEntry.isEnabled())
                anyEntryEnabled = true;

        if(anyEntryEnabled) {
            for (FluidListWidget.Entry entry : fluidList.children())
                if (entry instanceof FluidListWidget.FluidEntry fluidEntry)
                    fluidEntry.updateInList(parent.fluids, parent.disabledEnforcedFluids);
        } else {
            client.getToastManager().add(new SystemToast(
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    new TranslatableText("fluidlogged.fluid_config.error_toast.title"),
                    new TranslatableText("fluidlogged.fluid_config.error_toast.description")
            ));
        }
        client.setScreen(parent);
    }
}
