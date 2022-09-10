package de.leximon.fluidlogged.core.screen;

import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private static final List<OrderedText> COMPATIBILITY_MODE_TOOLTIP = MinecraftClient.getInstance().textRenderer.wrapLines(
            Text.translatable("fluidlogged.config.compatibilityMode.tooltip"),
            200
    );

    private final Screen parent;
    private List<OrderedText> warningText;
    private CyclingButtonWidget<Boolean> compatibilityModeButton;
    private ButtonWidget saveButton;

    protected boolean compatibilityMode;
    protected List<Identifier> fluids;
    protected List<Identifier> disabledEnforcedFluids;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("fluidlogged.config.title"));
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
        warningText = textRenderer.wrapLines(Text.translatable("fluidlogged.config.warning"), (int) (width * 0.8));

        addDrawableChild(new ButtonWidget(
                startX - 100, startY,
                200, 20,
                Text.translatable("fluidlogged.config.fluids"),
                button -> client.setScreen(new FluidConfigScreen(this))
        ));
        compatibilityModeButton = addDrawableChild(CyclingButtonWidget.onOffBuilder(compatibilityMode).build(
                startX - 100, startY + space,
                200, 20,
                Text.translatable("fluidlogged.config.compatibilityMode"),
                (button, value) -> {
                    this.compatibilityMode = value;
                    updateSaveButton();
                }
        ));

        saveButton = addDrawableChild(new ButtonWidget(
                startX - 100, startY + space*3,
                200, 20,
                Text.translatable("fluidlogged.config.save"),
                button -> saveAndClose(wereChangesMade())
        ));
        updateSaveButton();
        addDrawableChild(new ButtonWidget(
                startX - 100, startY + space*4,
                200, 20,
                Text.translatable("fluidlogged.config.cancel"),
                button -> close()
        ));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if(wereChangesMade()) {
            client.setScreen(new ConfirmSaveScreen(this, save -> {
                if(save)
                    saveAndClose(true);
                else
                    close();
            }));
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
        if (warningText != null) {
            int i = 0;
            for (OrderedText line : warningText) {
                textRenderer.drawWithShadow(matrices, line, (float) ((width / 2) - textRenderer.getWidth(line) / 2), 17 + 13 + 9 * i, 0xffffb617);
                i++;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);

        if(compatibilityModeButton.isHovered())
            renderOrderedTooltip(matrices, COMPATIBILITY_MODE_TOOLTIP, mouseX, mouseY);

    }

    private void saveAndClose(boolean changed) {
        FluidloggedConfig.compatibilityMode = this.compatibilityMode;
        FluidloggedConfig.fluids.clear();
        FluidloggedConfig.fluids.addAll(this.fluids);
        FluidloggedConfig.disabledEnforcedFluids.clear();
        FluidloggedConfig.disabledEnforcedFluids.addAll(this.disabledEnforcedFluids);
        FluidloggedConfig.saveConfig();

        if(changed)
            client.setScreen(new RestartRequiredScreen(parent));
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
