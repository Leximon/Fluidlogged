package de.leximon.fluidlogged.mixin.classes.sodium_compat;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.sodium_compat.ClonedChunkSectionExtension;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me/jellysquid/mods/sodium/client/world/cloned/ClonedChunkSection")
public class ClonedChunkSectionMixin implements ClonedChunkSectionExtension {

    @Unique @Nullable
    private Int2ReferenceMap<FluidState> fluidlogged$fluidData;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void injectFluidData(Level world, LevelChunk chunk, LevelChunkSection section, SectionPos chunkCoord, CallbackInfo ci) {
        if (section == null)
            return;

        Int2ReferenceOpenHashMap<FluidState> fluidData = null;

        for (Short2ObjectMap.Entry<FluidState> entry : ((LevelChunkSectionExtension) section).getFluidStates().short2ObjectEntrySet()) {
            short pos = entry.getShortKey();
            FluidState fluidState = entry.getValue();

            if (fluidData == null) {
                fluidData = new Int2ReferenceOpenHashMap<>();
                fluidData.defaultReturnValue(Fluids.EMPTY.defaultFluidState());
            }

            fluidData.put(WorldSlice.getLocalBlockIndex(pos >> 8 & 15, pos >> 4 & 15, pos & 15), fluidState);
        }

        this.fluidlogged$fluidData = fluidData;
    }

    @Override
    public @Nullable Int2ReferenceMap<FluidState> getFluidlogged$fluidData() {
        return this.fluidlogged$fluidData;
    }
}
