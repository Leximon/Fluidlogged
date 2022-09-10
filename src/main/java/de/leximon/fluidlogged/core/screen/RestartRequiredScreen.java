package de.leximon.fluidlogged.core.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
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
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
