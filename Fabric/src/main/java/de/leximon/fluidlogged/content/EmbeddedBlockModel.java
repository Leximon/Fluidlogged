package de.leximon.fluidlogged.content;

import de.leximon.fluidlogged.mixin.classes.fabric.StateHolderAccessor;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EmbeddedBlockModel implements BakedModel, FabricBakedModel {

    private final BakedModel parent;

    public EmbeddedBlockModel(BakedModel parent) {
        this.parent = parent;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.bakedModelConsumer().accept(this, state);

        BlockEntity blockEntity = blockView.getBlockEntity(pos);
        if (!(blockEntity instanceof EmbeddedBlockEntity embeddedBlockEntity))
            return;

        BlockState content = embeddedBlockEntity.getContent();
        if (content == null)
            return;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(content.getBlock());
        String variant = content.getValues().entrySet().stream()
                .map(StateHolderAccessor.fluidlogged$PROPERTY_ENTRY_TO_STRING_FUNCTION())
                .collect(Collectors.joining(","));

        ModelResourceLocation contentModelId = new ModelResourceLocation(blockId.getNamespace(), blockId.getPath(), variant);

        BakedModel contentModel = Minecraft.getInstance().getModelManager().getModel(contentModelId);
        context.pushTransform(quad -> {
            for (int i = 0; i < 4; i++) {
                float x = (quad.x(i) - 0.5f) * 1.001f + 0.5f;
                float y = (quad.y(i) - 0.5f) * 1.001f + 0.5f;
                float z = (quad.z(i) - 0.5f) * 1.001f + 0.5f;

                quad.pos(i, x, y, z);
            }
            return true;
        });
        context.bakedModelConsumer().accept(contentModel, content);
        context.popTransform();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, @NotNull RandomSource randomSource) {
        return this.parent.getQuads(blockState, direction, randomSource);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.parent.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.parent.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.parent.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.parent.isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.parent.getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return this.parent.getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return this.parent.getOverrides();
    }
}
