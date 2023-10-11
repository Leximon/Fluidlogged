package de.leximon.fluidlogged.mixin.classes.storing_and_access;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess implements LevelChunkExtension {

    @Shadow @Final Level level;

    public LevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Override
    public FluidState setFluidState(BlockPos blockPos, FluidState fluidState) {
        int y = blockPos.getY();

        LevelChunkSection levelChunkSection = this.getSection(this.getSectionIndex(y));
        boolean hasOnlyAir = levelChunkSection.hasOnlyAir();
        if (levelChunkSection.hasOnlyAir() && fluidState.isEmpty())
            return null;

        int rx = blockPos.getX() & 15;
        int ry = y & 15;
        int rz = blockPos.getZ() & 15;

        FluidState prevFluidState = ((LevelChunkSectionExtension) levelChunkSection).setFluidState(rx, ry, rz, fluidState);
        if (prevFluidState == fluidState)
            return null;

        BlockState blockState = levelChunkSection.getBlockState(rx, ry, rz);
        this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR).update(rx, ry, rz, blockState);
        this.heightmaps.get(Heightmap.Types.WORLD_SURFACE).update(rx, ry, rz, blockState);

        boolean newHasOnlyAir = levelChunkSection.hasOnlyAir();
        if (hasOnlyAir != newHasOnlyAir)
            this.level.getChunkSource().getLightEngine().updateSectionStatus(blockPos, newHasOnlyAir);

        if (Fluidlogged.Internal.hasDifferentLightEmission(prevFluidState, fluidState)) {
            ProfilerFiller profilerFiller = this.level.getProfiler();
            profilerFiller.push("updateSkyLightSources");
            this.skyLightSources.update(this, rx, ry, rz);
            profilerFiller.popPush("queueCheckLight");
            this.level.getChunkSource().getLightEngine().checkBlock(blockPos);
            profilerFiller.pop();
        }

        this.unsaved = true;
        return prevFluidState;
    }
}
