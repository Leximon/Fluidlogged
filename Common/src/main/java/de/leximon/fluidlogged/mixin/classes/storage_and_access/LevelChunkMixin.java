package de.leximon.fluidlogged.mixin.classes.storage_and_access;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements LevelChunkExtension {

    public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Override
    public void setFluidState(BlockPos blockPos, FluidState fluidState) {
        int y = blockPos.getY();

        LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndex(y));
        if (levelChunkSection.hasOnlyAir())
            return;

        int rx = blockPos.getX() & 15;
        int ry = y & 15;
        int rz = blockPos.getZ() & 15;

        ((LevelChunkSectionExtension) levelChunkSection).setFluidState(rx, ry, rz, fluidState);
        unsaved = true;
    }
}
