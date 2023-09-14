package de.leximon.fluidlogged.mixin.classes.compat_sodium;

import de.leximon.fluidlogged.mixin.extensions.compat_sodium.WorldSliceExtension;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me/jellysquid/mods/sodium/client/render/chunk/compile/tasks/ChunkBuilderMeshingTask")
public class ChunkBuilderMeshingTaskMixin {

    @Unique private FluidState fluidlogged$fluidState;

    @Redirect(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0
            ),
            remap = false
    )
    private BlockState redirectCaptureFluidState(WorldSlice instance, int x, int y, int z) {
        fluidlogged$fluidState = ((WorldSliceExtension) (Object) instance).fluidlogged$getFluidState(x, y, z);
        return instance.getBlockState(x, y, z);
    }

    @Redirect(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
                    ordinal = 0
            )
    )
    private boolean redirectIsAir(BlockState instance) {
        return instance.isAir() && fluidlogged$fluidState.isEmpty();
    }

    @Redirect(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"
            )
    )
    private boolean redirectIsEmpty(FluidState instance) {
        return fluidlogged$fluidState.isEmpty();
    }

    @ModifyArg(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;render(Lme/jellysquid/mods/sodium/client/world/WorldSlice;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
            ),
            index = 1,
            remap = false
    )
    private FluidState modifyPassedFluidState(FluidState fluidState) {
        return fluidState.isEmpty() ? fluidlogged$fluidState : fluidState;
    }

}
