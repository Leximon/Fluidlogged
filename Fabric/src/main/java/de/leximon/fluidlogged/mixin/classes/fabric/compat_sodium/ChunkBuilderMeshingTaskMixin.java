package de.leximon.fluidlogged.mixin.classes.fabric.compat_sodium;

import de.leximon.fluidlogged.mixin.extensions.compat_sodium.WorldSliceExtension;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
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
            )
    )
    private BlockState redirectCaptureFluidStateFabric(WorldSlice instance, int x, int y, int z) {
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

    @Redirect(
            method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;",
                    ordinal = 0
            )
    )
    private FluidState modifyPassedFluidState(BlockState instance) {
        FluidState fluidState = instance.getFluidState();
        return fluidState.isEmpty() ? fluidlogged$fluidState : fluidState;
    }

}
