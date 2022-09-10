package de.leximon.fluidlogged.core.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
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
                Text.translatable("fluidlogged.restart_required.title").styled(s -> s.withColor(Formatting.RED)),
                Text.translatable("fluidlogged.restart_required.message"),
                Text.translatable("fluidlogged.restart_required.quit"),
                Text.translatable("fluidlogged.restart_required.later")
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
