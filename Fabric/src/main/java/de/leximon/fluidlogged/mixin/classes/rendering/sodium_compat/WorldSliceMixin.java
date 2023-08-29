package de.leximon.fluidlogged.mixin.classes.rendering.sodium_compat;

import de.leximon.fluidlogged.mixin.extensions.sodium_compat.ClonedChunkSectionExtension;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(targets = "me/jellysquid/mods/sodium/client/world/WorldSlice")
public abstract class WorldSliceMixin {

    @Shadow @Final private static int SECTION_ARRAY_SIZE;

    @Shadow private int originX;
    @Shadow private int originY;
    @Shadow private int originZ;

    @Shadow public static int getLocalSectionIndex(int x, int y, int z) { return 0; }

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow public static int getLocalBlockIndex(int x, int y, int z) { return 0; }

    @Unique
    private Int2ReferenceMap<FluidState>[] fluidArrays;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void injectInit(ClientLevel world, CallbackInfo ci) {
        this.fluidArrays = new Int2ReferenceMap[SECTION_ARRAY_SIZE];
    }

    @Inject(method = "copySectionData", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void injectUnpackFluidData(ChunkRenderContext context, int sectionIndex, CallbackInfo ci, ClonedChunkSection section) {
        ClonedChunkSectionExtension sectionExt = ((ClonedChunkSectionExtension) section);
        if (sectionExt.getFluidData() == null)
            return;

        this.fluidArrays[sectionIndex] = sectionExt.getFluidData();
    }

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        FluidState fluidState = getBlockState(pos).getFluidState();
        if (!fluidState.isEmpty())
            return fluidState;

        int relX = pos.getX() - this.originX;
        int relY = pos.getY() - this.originY;
        int relZ = pos.getZ() - this.originZ;

        Int2ReferenceMap<FluidState> fluids = this.fluidArrays[getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4)];
        if (fluids == null)
            return Fluids.EMPTY.defaultFluidState();

        return fluids.get(getLocalBlockIndex(relX & 15, relY & 15, relZ & 15));
    }

}
