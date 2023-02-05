package de.leximon.fluidlogged.core.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;

public class FluidListWidget extends ContainerObjectSelectionList<FluidListWidget.Entry> {

    private static final ResourceLocation WATER_ID = new ResourceLocation("water");

    private final FluidConfigScreen parent;

    public FluidListWidget(FluidConfigScreen parent, Minecraft client) {
        super(client, parent.width + 45, parent.height, 28, parent.height - (32 + 9), 24);
        this.parent = parent;


        boolean enforcedCategoryAdded = false;
        for (Map.Entry<ResourceKey<Fluid>, Fluid> entry : BuiltInRegistries.FLUID.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            Fluid fluid = entry.getValue();
            if (!FluidloggedConfig.enforcedFluids.contains(id))
                continue;

            if(!enforcedCategoryAdded) {
                addEntry(new CategoryEntry(Component.translatable("fluidlogged.fluid_config.category.enforced")));
                enforcedCategoryAdded = true;
            }
            addEntry(new FluidEntry(id, fluid, true, !parent.parent.disabledEnforcedFluids.contains(id)));
        }
        boolean otherCategoryAdded = false;
        for (Map.Entry<ResourceKey<Fluid>, Fluid> entry : BuiltInRegistries.FLUID.entrySet()) {
            ResourceLocation id = entry.getKey().location();
            Fluid fluid = entry.getValue();
            if(FluidloggedConfig.enforcedFluids.contains(id))
                continue;

            boolean inConfig = parent.parent.fluids.contains(id);
            if(!fluid.isSource(fluid.defaultFluidState()) && !inConfig || id.equals(WATER_ID))
                continue; // only show still fluids but if a flowing fluid is specified in the config for whatever reason, show it anyway
            if(!otherCategoryAdded) {
                addEntry(new CategoryEntry(Component.translatable("fluidlogged.fluid_config.category.other")));
                otherCategoryAdded = true;
            }
            addEntry(new FluidEntry(id, fluid, false, inConfig));
        }
    }

    public void updateSize() {
        super.updateSize(parent.width + 45, parent.height, 28, parent.height - (32 + 9));
    }

    public static abstract class Entry extends ContainerObjectSelectionList.Entry<de.leximon.fluidlogged.core.screen.FluidListWidget.Entry> { }

    public class CategoryEntry extends de.leximon.fluidlogged.core.screen.FluidListWidget.Entry {

        private final FormattedCharSequence name;

        public CategoryEntry(Component name) {
            this.name = name.getVisualOrderText();
        }

        @Override
        public List<? extends NarratableEntry> narratables() { return ImmutableList.of(); }

        @Override
        public List<? extends GuiEventListener> children() { return ImmutableList.of(); }

        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int textWidth = minecraft.font.width(name);
            minecraft.font.draw(matrices, name, (parent.width - textWidth) / 2f, y + (entryHeight - 9) / 2f + 4, 0xffffffff);
        }
    }

    public class FluidEntry extends de.leximon.fluidlogged.core.screen.FluidListWidget.Entry {

        private final ResourceLocation id;
        private final boolean enforced;
        private final ItemStack icon;
        private final Component name;
        private final Component idText;
        private final Checkbox checkbox;

        private FluidEntry(ResourceLocation id, Fluid fluid, boolean enforced, boolean enabled) {
            LiquidBlock block = Fluidlogged.fluidBlocks.get(fluid);
            this.name = block == null ? null : Component.translatable(block.getDescriptionId());
            Item item = fluid.getBucket();
            this.icon = item == null ? ItemStack.EMPTY : new ItemStack(item);
            this.idText = Component.literal(id.toString());
            this.checkbox = new Checkbox(0, 0, 20, 20, Component.empty(), enabled, false);
            this.id = id;
            this.enforced = enforced;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(checkbox);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(checkbox);
        }

        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            checkbox.setX(x);
            checkbox.setY(y);
            checkbox.render(matrices, mouseX, mouseY, tickDelta);
            if(name == null) {
                minecraft.font.draw(matrices, idText, x + 42, y + (entryHeight - 9) / 2f, 0xff5c5c5c);
            } else {
                minecraft.font.draw(matrices, name, x + 42, y + 2, 0xffffffff);
                minecraft.font.draw(matrices, idText, x + 42, y + 11, 0xff5c5c5c);
            }

            if(icon != null)
                minecraft.getItemRenderer().renderGuiItem(icon, x + 22, y + 2);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return checkbox.mouseClicked(mouseX, mouseY, button);
        }

        public boolean isEnabled() {
            return checkbox.selected();
        }

        public ResourceLocation getId() {
            return id;
        }

        public void updateInList(List<ResourceLocation> fluids, List<ResourceLocation> disabledEnforcedFluids) {
            List<ResourceLocation> list = enforced ? disabledEnforcedFluids : fluids;
            boolean add = isEnabled() ^ enforced; // invert if enforced
            if (add) {
                if (!list.contains(id))
                    list.add(id);
                return;
            }
            list.remove(id);
        }
    }

}
