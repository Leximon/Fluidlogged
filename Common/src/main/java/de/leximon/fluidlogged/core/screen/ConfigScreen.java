package de.leximon.fluidlogged.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ConfigScreen extends Screen {

    private static final List<FormattedCharSequence> COMPATIBILITY_MODE_TOOLTIP = Minecraft.getInstance().font.split(
            Component.translatable("fluidlogged.config.compatibilityMode.tooltip"),
            200
    );

    private final Screen parent;
    private List<FormattedCharSequence> warningText;
    private CycleButton<Boolean> compatibilityModeButton;
    private Button saveButton;

    protected boolean compatibilityMode;
    protected List<ResourceLocation> fluids;
    protected List<ResourceLocation> disabledEnforcedFluids;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("fluidlogged.config.title"));
        this.parent = parent;
        this.compatibilityMode = FluidloggedConfig.compatibilityMode;
        this.fluids = new ArrayList<>(FluidloggedConfig.fluids);
        this.disabledEnforcedFluids = new ArrayList<>(FluidloggedConfig.disabledEnforcedFluids);
    }

    @Override
    protected void init() {
        int startX = this.width / 2;
        int startY = this.height / 4 + 5;
        int space = 24;
        warningText = font.split(Component.translatable("fluidlogged.config.warning"), (int) (width * 0.8));

        addRenderableWidget(
                Button.builder(
                                Component.translatable("fluidlogged.config.fluids"),
                                button -> minecraft.setScreen(new FluidConfigScreen(this))
                        )
                        .size(200, 20)
                        .pos(startX - 100, startY)
                        .build()
        );
        compatibilityModeButton = addRenderableWidget(CycleButton.onOffBuilder(compatibilityMode).create(
                startX - 100, startY + space,
                200, 20,
                Component.translatable("fluidlogged.config.compatibilityMode"),
                (button, value) -> {
                    this.compatibilityMode = value;
                    updateSaveButton();
                }
        ));

        saveButton = addRenderableWidget(
                Button.builder(
                                Component.translatable("fluidlogged.config.save"),
                                button -> saveAndClose(wereChangesMade())
                        )
                        .size(200, 20)
                        .pos(startX - 100, startY + space * 3)
                        .build()
        );

        updateSaveButton();
        addRenderableWidget(
                Button.builder(
                                Component.translatable("fluidlogged.config.cancel"),
                                button -> onClose()
                        )
                        .size(200, 20)
                        .pos(startX - 100, startY + space * 4)
                        .build()
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if(wereChangesMade()) {
            minecraft.setScreen(new ConfirmSaveScreen(this, save -> {
                if(save)
                    saveAndClose(true);
                else
                    onClose();
            }));
            return false;
        }
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }


    @Override
    public void renderBackground(GuiGraphics g) {
        renderDirtBackground(g);
    }


    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 17, 16777215);
        if (warningText != null) {
            int i = 0;
            for (FormattedCharSequence line : warningText) {
                g.drawString(font, line, ((width / 2) - font.width(line) / 2), 17 + 13 + 9 * i, 0xffffb617, true);
                i++;
            }
        }

        super.render(g, mouseX, mouseY, delta);

        if (compatibilityModeButton.isHoveredOrFocused())
            g.renderTooltip(font, COMPATIBILITY_MODE_TOOLTIP, mouseX, mouseY);
    }

    private void saveAndClose(boolean changed) {
        FluidloggedConfig.compatibilityMode = this.compatibilityMode;
        FluidloggedConfig.fluids.clear();
        FluidloggedConfig.fluids.addAll(this.fluids);
        FluidloggedConfig.disabledEnforcedFluids.clear();
        FluidloggedConfig.disabledEnforcedFluids.addAll(this.disabledEnforcedFluids);
        FluidloggedConfig.saveConfig();

        if(changed)
            minecraft.setScreen(new RestartRequiredScreen(parent));
    }

    private boolean wereChangesMade() {
        return this.compatibilityMode != FluidloggedConfig.compatibilityMode
                || !this.fluids.equals(FluidloggedConfig.fluids)
                || !this.disabledEnforcedFluids.equals(FluidloggedConfig.disabledEnforcedFluids);
    }

    private void updateSaveButton() {
        saveButton.active = wereChangesMade();
    }
}
