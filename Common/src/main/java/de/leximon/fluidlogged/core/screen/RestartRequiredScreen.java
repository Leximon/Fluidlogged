package de.leximon.fluidlogged.core.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class RestartRequiredScreen extends ConfirmScreen {

    public RestartRequiredScreen(Screen nextScreen) {
        super(
                v -> {
                    if(v)
                        Minecraft.getInstance().stop();
                    else
                        Minecraft.getInstance().setScreen(nextScreen);
                },
                new TranslatableComponent("fluidlogged.restart_required.title").withStyle(s -> s.withColor(ChatFormatting.RED)),
                new TranslatableComponent("fluidlogged.restart_required.message"),
                new TranslatableComponent("fluidlogged.restart_required.quit"),
                new TranslatableComponent("fluidlogged.restart_required.later")
        );
    }

    @Override
    public void renderBackground(PoseStack matrices) {
        this.renderDirtBackground(0);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
