package de.leximon.fluidlogged.core.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class RestartRequiredScreen extends ConfirmScreen {

    public RestartRequiredScreen(Screen nextScreen) {
        super(
                v -> {
                    if(v)
                        MinecraftClient.getInstance().scheduleStop();
                    else
                        MinecraftClient.getInstance().setScreen(nextScreen);
                },
                new TranslatableText("fluidlogged.restart_required.title").styled(s -> s.withColor(Formatting.RED)),
                new TranslatableText("fluidlogged.restart_required.message"),
                new TranslatableText("fluidlogged.restart_required.quit"),
                new TranslatableText("fluidlogged.restart_required.later")
        );
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        this.renderBackgroundTexture(0);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
