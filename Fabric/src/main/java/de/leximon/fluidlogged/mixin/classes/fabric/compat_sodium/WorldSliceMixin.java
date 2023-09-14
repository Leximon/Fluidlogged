package de.leximon.fluidlogged.mixin.classes.fabric.compat_sodium;

import de.leximon.fluidlogged.mixin.extensions.compat_sodium.ClonedChunkSectionExtension;
import de.leximon.fluidlogged.mixin.extensions.compat_sodium.WorldSliceExtension;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(targets = "me/jellysquid/mods/sodium/client/world/WorldSlice")
public abstract class WorldSliceMixin implements WorldSliceExtension {

    @Shadow @Final private static int SECTION_ARRAY_SIZE;

    @Shadow private int originX;
    @Shadow private int originY;
    @Shadow private int originZ;

    @Shadow public static int getLocalSectionIndex(int x, int y, int z) { return 0; }

    @Shadow public static int getLocalBlockIndex(int x, int y, int z) { return 0; }

    @Shadow public abstract BlockState getBlockState(int x, int y, int z);

    @Unique
    private Int2ReferenceMap<FluidState>[] fluidlogged$fluidArrays;

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void injectInit(ClientLevel world, CallbackInfo ci) {
        this.fluidlogged$fluidArrays = new Int2ReferenceMap[SECTION_ARRAY_SIZE];
    }

    @Inject(method = "copySectionData", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void injectUnpackFluidData(ChunkRenderContext context, int sectionIndex, CallbackInfo ci, ClonedChunkSection section) {
        ClonedChunkSectionExtension sectionExt = ((ClonedChunkSectionExtension) section);

        this.fluidlogged$fluidArrays[sectionIndex] = sectionExt.getFluidlogged$fluidData();
    }

    @Inject(
            method = "reset",
            at = @At(value = "FIELD", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;blockEntityArrays:[Lit/unimi/dsi/fastutil/ints/Int2ReferenceMap;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false
    )
    private void injectReset(CallbackInfo ci, int sectionIndex) {
        this.fluidlogged$fluidArrays[sectionIndex] = null;
    }


    @SuppressWarnings("OverwriteAuthorRequired") // fabric version doesn't want to compile because the reason is an unknown tag
    @Overwrite
    public FluidState getFluidState(BlockPos pos) {
        return fluidlogged$getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public FluidState fluidlogged$getFluidState(int x, int y, int z) {
        FluidState fluidState = getBlockState(x, y, z).getFluidState();
        if (!fluidState.isEmpty())
            return fluidState;

        int relX = x - this.originX;
        int relY = y - this.originY;
        int relZ = z - this.originZ;

        Int2ReferenceMap<FluidState> fluids = this.fluidlogged$fluidArrays[getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4)];
        if (fluids == null)
            return Fluids.EMPTY.defaultFluidState();

        return fluids.get(getLocalBlockIndex(relX & 15, relY & 15, relZ & 15));
    }
}
