package de.leximon.fluidlogged.core.screen;

import com.google.common.collect.ImmutableList;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class FluidListWidget extends ElementListWidget<FluidListWidget.Entry> {

    private static final Identifier WATER_ID = new Identifier("water");

    private final FluidConfigScreen parent;

    public FluidListWidget(FluidConfigScreen parent, MinecraftClient client) {
        super(client, parent.width + 45, parent.height, 28, parent.height - (32 + 9), 24);
        this.parent = parent;


        boolean enforcedCategoryAdded = false;
        for (Map.Entry<RegistryKey<Fluid>, Fluid> entry : Registries.FLUID.getEntrySet()) {
            Identifier id = entry.getKey().getValue();
            Fluid fluid = entry.getValue();
            if (!FluidloggedConfig.enforcedFluids.contains(id))
                continue;

            if(!enforcedCategoryAdded) {
                addEntry(new CategoryEntry(Text.translatable("fluidlogged.fluid_config.category.enforced")));
                enforcedCategoryAdded = true;
            }
            addEntry(new FluidEntry(id, fluid, true, !parent.parent.disabledEnforcedFluids.contains(id)));
        }
        boolean otherCategoryAdded = false;
        for (Map.Entry<RegistryKey<Fluid>, Fluid> entry : Registries.FLUID.getEntrySet()) {
            Identifier id = entry.getKey().getValue();
            Fluid fluid = entry.getValue();
            if(FluidloggedConfig.enforcedFluids.contains(id))
                continue;

            boolean inConfig = parent.parent.fluids.contains(id);
            if(!fluid.isStill(fluid.getDefaultState()) && !inConfig || id.equals(WATER_ID))
                continue; // only show still fluids but if a flowing fluid is specified in the config for whatever reason, show it anyway
            if(!otherCategoryAdded) {
                addEntry(new CategoryEntry(Text.translatable("fluidlogged.fluid_config.category.other")));
                otherCategoryAdded = true;
            }
            addEntry(new FluidEntry(id, fluid, false, inConfig));
        }
    }

    public void updateSize() {
        super.updateSize(parent.width + 45, parent.height, 28, parent.height - (32 + 9));
    }

    public static abstract class Entry extends ElementListWidget.Entry<Entry> { }

    public class CategoryEntry extends Entry {

        private final OrderedText name;

        public CategoryEntry(Text name) {
            this.name = name.asOrderedText();
        }

        @Override
        public List<? extends Selectable> selectableChildren() { return ImmutableList.of(); }

        @Override
        public List<? extends Element> children() { return ImmutableList.of(); }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int textWidth = client.textRenderer.getWidth(name);
            client.textRenderer.draw(matrices, name, (parent.width - textWidth) / 2f, y + (entryHeight - 9) / 2f + 4, 0xffffffff);
        }
    }

    public class FluidEntry extends Entry {

        private final Identifier id;
        private final boolean enforced;
        private final ItemStack icon;
        private final Text name;
        private final Text idText;
        private final CheckboxWidget checkbox;

        private FluidEntry(Identifier id, Fluid fluid, boolean enforced, boolean enabled) {
            FluidBlock block = Fluidlogged.fluidBlocks.get(fluid);
            this.name = block == null ? null : Text.translatable(block.getTranslationKey());
            Item item = fluid.getBucketItem();
            this.icon = item == null ? ItemStack.EMPTY : new ItemStack(item);
            this.idText = Text.literal(id.toString());
            this.checkbox = new CheckboxWidget(0, 0, 20, 20, Text.empty(), enabled, false);
            this.id = id;
            this.enforced = enforced;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(checkbox);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(checkbox);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            checkbox.setX(x);
            checkbox.setY(y);
            checkbox.render(matrices, mouseX, mouseY, tickDelta);
            if(name == null) {
                client.textRenderer.draw(matrices, idText, x + 42, y + (entryHeight - 9) / 2f, 0xff5c5c5c);
            } else {
                client.textRenderer.draw(matrices, name, x + 42, y + 2, 0xffffffff);
                client.textRenderer.draw(matrices, idText, x + 42, y + 11, 0xff5c5c5c);
            }

            if(icon != null)
                client.getItemRenderer().renderGuiItemIcon(icon, x + 22, y + 2);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return checkbox.mouseClicked(mouseX, mouseY, button);
        }

        public boolean isEnabled() {
            return checkbox.isChecked();
        }

        public Identifier getId() {
            return id;
        }

        public void updateInList(List<Identifier> fluids, List<Identifier> disabledEnforcedFluids) {
            List<Identifier> list = enforced ? disabledEnforcedFluids : fluids;
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
