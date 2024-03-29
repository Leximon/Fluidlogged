package de.leximon.fluidlogged.mixin.classes.world_interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin extends LightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage>{

    @Shadow @Final private BlockPos.MutableBlockPos mutablePos;

    @Unique private FluidState fluidloggedFluidState;

    protected BlockLightEngineMixin(LightChunkGetter lightChunkGetter, BlockLightSectionStorage layerLightSectionStorage) {
        super(lightChunkGetter, layerLightSectionStorage);
    }

    @Inject(method = "checkNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;getState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.AFTER))
    private void injectCaptureFluidBlock(long l, CallbackInfo ci) {
        int i = SectionPos.blockToSectionCoord(this.mutablePos.getX());
        int j = SectionPos.blockToSectionCoord(this.mutablePos.getZ());
        LightChunk lightChunk = this.getChunk(i, j);

        if (lightChunk == null) {
            this.fluidloggedFluidState = Fluids.EMPTY.defaultFluidState();
            return;
        }

        FluidState fluidState = lightChunk.getFluidState(this.mutablePos);
        if (fluidState == null) { // fixes a crash with the create mod, not ideal, but it works without any problems
            this.fluidloggedFluidState = Fluids.EMPTY.defaultFluidState();
            return;
        }

        this.fluidloggedFluidState = fluidState;
    }


    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getEmission", at = @At("STORE"), ordinal = 0)
    private int injectFluidLight(int i) {
        return Math.max(i, this.fluidloggedFluidState.createLegacyBlock().getLightEmission());
    }

}
