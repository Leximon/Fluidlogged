package de.leximon.fluidlogged.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class FluidConfigScreen extends Screen {

    protected final ConfigScreen parent;

    private FluidListWidget fluidList;

    protected FluidConfigScreen(ConfigScreen parent) {
        super(Component.translatable("fluidlogged.fluid_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (fluidList == null)
            fluidList = new FluidListWidget(this, minecraft);
        else
            fluidList.updateSize();
        addWidget(fluidList);

        addRenderableWidget(new Button(
                this.width / 2 - 75, this.height - 29,
                150, 20,
                CommonComponents.GUI_DONE, button -> onClose()
        ));
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        this.renderDirtBackground(0);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.fluidList.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 12, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }


    @Override
    public void onClose() {
        boolean anyEntryEnabled = false;
        for (FluidListWidget.Entry entry : fluidList.children())
            if (entry instanceof FluidListWidget.FluidEntry fluidEntry && fluidEntry.isEnabled())
                anyEntryEnabled = true;

        if (anyEntryEnabled) {
            for (FluidListWidget.Entry entry : fluidList.children())
                if (entry instanceof FluidListWidget.FluidEntry fluidEntry)
                    fluidEntry.updateInList(parent.fluids, parent.disabledEnforcedFluids);
        } else {
            minecraft.getToasts().addToast(new SystemToast(
                    SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                    Component.translatable("fluidlogged.fluid_config.error_toast.title"),
                    Component.translatable("fluidlogged.fluid_config.error_toast.description")
            ));
        }
        minecraft.setScreen(parent);
    }
}
