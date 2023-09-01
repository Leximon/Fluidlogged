package de.leximon.fluidlogged.mixin.classes.storing_and_access;

import com.mojang.serialization.Codec;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {

    @Shadow @Final private static Logger LOGGER;


    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectRead(ServerLevel serverLevel, PoiManager poiManager, ChunkPos chunkPos, CompoundTag compoundTag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos2, UpgradeData upgradeData, boolean bl, ListTag listTag, int i, LevelChunkSection[] levelChunkSections, boolean bl2, ChunkSource chunkSource, LevelLightEngine levelLightEngine, Registry registry, Codec codec, boolean bl3, int j, CompoundTag compoundTag2, int k, int l, PalettedContainer palettedContainer, PalettedContainerRO palettedContainerRO, LevelChunkSection levelChunkSection, SectionPos sectionPos) {
        Short2ObjectMap<FluidState> container = ((LevelChunkSectionExtension) levelChunkSection).createAndSetFluidStatesMap();
        if (compoundTag2.contains("fluidlogged.fluid_states", Tag.TAG_COMPOUND)) {
            CompoundTag fluidStates = compoundTag2.getCompound("fluidlogged.fluid_states");

            // read the palette
            ListTag paletteTag = fluidStates.getList("palette", Tag.TAG_COMPOUND);
            IdMapper<FluidState> palette = new IdMapper<>(paletteTag.size());
            for (int i1 = 0; i1 < paletteTag.size(); i1++)
                palette.add(FluidState.CODEC.parse(NbtOps.INSTANCE, paletteTag.getCompound(i1)).getOrThrow(false, LOGGER::error));

            // read the data
            int[] data = fluidStates.getIntArray("data");
            for (int value : data) {
                short pos = (short) ((value >> 16) & 0xFFFF);
                short id = (short) (value & 0xFFFF);
                FluidState state = palette.byId(id);
                if (state == null) {
                    LOGGER.error("Invalid fluid state id {} at {}", id, pos);
                    continue;
                }
                container.put(pos, state);
            }
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectWrite(ServerLevel serverLevel, ChunkAccess chunkAccess, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag compoundTag, BlendingData blendingData, BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, LevelChunkSection[] levelChunkSections, ListTag listTag, LevelLightEngine levelLightEngine, Registry registry, Codec codec, boolean bl, int i, int j, boolean bl2, DataLayer dataLayer, DataLayer dataLayer2, CompoundTag compoundTag2, LevelChunkSection levelChunkSection) {
        Short2ObjectMap<FluidState> container = ((LevelChunkSectionExtension) levelChunkSection).getFluidStates();
        CompoundTag fluidStates = new CompoundTag();

        // fill the palette
        IdMapper<FluidState> palette = new IdMapper<>();
        for (FluidState value : container.values())
            if (palette.getId(value) == -1)
                palette.add(value);

        // write the data
        int[] data = new int[container.size()];
        int index = 0;
        for (Short2ObjectMap.Entry<FluidState> entry : container.short2ObjectEntrySet()) {
            short pos = entry.getShortKey();
            short state = (short) palette.getId(entry.getValue());
            data[index++] = (pos << 16) | state;
        }
        fluidStates.putIntArray("data", data);

        // write the palette
        ListTag paletteTag = new ListTag();
        for (FluidState state : palette)
            paletteTag.add(FluidState.CODEC.encodeStart(NbtOps.INSTANCE, state).getOrThrow(false, LOGGER::error));
        fluidStates.put("palette", paletteTag);

        compoundTag2.put("fluidlogged.fluid_states", fluidStates);
    }
}
