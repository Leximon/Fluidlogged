package de.leximon.fluidlogged.config;

import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.Component;

public class YaclMissingScreen extends WarningScreen {

    private static final Component TITLE = Component.translatable("fluidlogged.config.yacl_missing", Services.PLATFORM.getConfigPath().toString()).withStyle(ChatFormatting.BOLD);
    private static final Component CONTENT = Component.translatable("fluidlogged.config.yacl_missing.desc");
    private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);

    private final Screen parent;

    public YaclMissingScreen(Screen parent) {
        super(TITLE, CONTENT, NARRATION);
        this.parent = parent;
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected void initButtons(int yOffset) {
        this.addRenderableWidget(
                Button.builder(Component.literal(""),
                                button -> this.minecraft.setScreen(this.parent))
                        .build()
        );
    }
}
