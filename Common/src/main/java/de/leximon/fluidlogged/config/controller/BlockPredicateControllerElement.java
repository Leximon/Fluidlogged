package de.leximon.fluidlogged.config.controller;

import de.leximon.fluidlogged.Fluidlogged;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Consumer;

public class BlockPredicateControllerElement extends StringControllerElement {

    private static final ResourceLocation TAG_ICON_LOCATION = Fluidlogged.id("textures/tag_icon.png");

    private PreviewState previewState;
    private ItemStack blockPreview;

    public BlockPredicateControllerElement(IStringController<?> control, YACLScreen screen, Dimension<Integer> dim, boolean instantApply) {
        super(control, screen, dim, instantApply);
        updateItemPreview();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        Dimension<Integer> dimension = getDimension();

        switch (this.previewState) {
            case VALID_BLOCK -> {
                if (this.blockPreview != null)
                    graphics.renderFakeItem(this.blockPreview, dimension.x() + 8, dimension.y() + 2);
            }
            case VALID_TAG -> {
                graphics.blit(TAG_ICON_LOCATION, dimension.x() + 8, dimension.y() + 2, 0, 0, 16, 16, 16, 16);
            }
            case INVALID -> {
                graphics.drawString(this.textRenderer, Component.literal("?"), dimension.x() + 13, dimension.y() + 6, getValueColor());
            }
        }
    }

    @Override
    protected int getValueColor() {
        return this.previewState == PreviewState.INVALID ? ChatFormatting.RED.getColor() : super.getValueColor();
    }

    private void updateItemPreview() {
        if (this.inputField.startsWith("#")) {
            this.previewState = PreviewState.VALID_TAG;
            return;
        }

        ResourceLocation id = ResourceLocation.tryParse(this.inputField);
        Optional<Block> blockOpt = Optional.empty();
        if (id != null)
            blockOpt = BuiltInRegistries.BLOCK.getOptional(id);

        if (blockOpt.isEmpty()) {
            this.previewState = PreviewState.INVALID;
            return;
        }

        Block block = blockOpt.get();
        this.previewState = PreviewState.VALID_BLOCK;
        this.blockPreview = block.getCloneItemStack(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, block.defaultBlockState());
    }

    @Override
    public void write(String string) {
        super.write(string);
        updateItemPreview();
    }

    @Override
    public boolean modifyInput(Consumer<StringBuilder> consumer) {
        boolean success = super.modifyInput(consumer);
        updateItemPreview();
        return success;
    }

    public enum PreviewState {
        VALID_BLOCK,
        VALID_TAG,
        INVALID
    }
}
