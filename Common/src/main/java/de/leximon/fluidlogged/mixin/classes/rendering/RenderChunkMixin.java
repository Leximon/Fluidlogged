package de.leximon.fluidlogged.mixin.classes.rendering;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.RenderChunkExtension;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderChunk.class)
public class RenderChunkMixin implements RenderChunkExtension {

    @Shadow @Final private LevelChunk wrapped;
    @Shadow @Final private @Nullable List<PalettedContainer<BlockState>> sections;

    @Unique private List<Short2ObjectMap<FluidState>> fluidStatesSections;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectInit(LevelChunk levelChunk, CallbackInfo ci) {
        if (levelChunk instanceof EmptyLevelChunk)
            return;

        LevelChunkSection[] levelChunkSections = levelChunk.getSections();
        this.fluidStatesSections = new ArrayList<>(levelChunkSections.length);

        for (LevelChunkSection levelChunkSection : levelChunkSections) {
            this.fluidStatesSections.add(levelChunkSection.hasOnlyAir()
                    ? null
                    : new Short2ObjectOpenHashMap<>(((LevelChunkSectionExtension) levelChunkSection).getFluidStates())
            );
        }
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        if (this.sections == null)
            return Fluids.EMPTY.defaultFluidState();

        try {
            int sectionIndex = this.wrapped.getSectionIndex(y);
            if (sectionIndex < 0 || sectionIndex >= this.sections.size())
                return Fluids.EMPTY.defaultFluidState();

            PalettedContainer<BlockState> palettedContainer = this.sections.get(sectionIndex);
            if (palettedContainer == null)
                return Fluids.EMPTY.defaultFluidState();

            BlockState blockState = palettedContainer.get(x & 15, y & 15, z & 15);
            FluidState defaultFluidState = blockState.getFluidState();

            if (!defaultFluidState.isEmpty())
                return defaultFluidState;

            Short2ObjectMap<FluidState> fluidStates = this.fluidStatesSections.get(sectionIndex);
            if (fluidStates == null)
                return defaultFluidState;

            FluidState fluidState = fluidStates.get((short) ((x & 15) << 8 | (y & 15) << 4 | (z & 15)));
            if (fluidState == null)
                return defaultFluidState;

            return fluidState;
        } catch (Throwable var8) {
            CrashReport crashReport = CrashReport.forThrowable(var8, "Getting block state");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being got");
            crashReportCategory.setDetail("Location", () -> CrashReportCategory.formatLocation(this.wrapped, x, y, z));
            throw new ReportedException(crashReport);
        }
    }

}
