package de.leximon.fluidlogged.mixin.classes.compat.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.leximon.fluidlogged.mixin.extensions.compat_sodium.WorldSliceExtension;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/caffeinemc/mods/sodium/client/render/chunk/compile/tasks/ChunkBuilderMeshingTask")
public class ChunkBuilderMeshingTaskMixin {

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0
            )
    )
    private void shareFluidState(
            ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local LevelSlice slice,
            @Local(ordinal = 8) int x,
            @Local(ordinal = 6) int y,
            @Local(ordinal = 7) int z,
            @Share("fluidState") LocalRef<FluidState> fluidState
    ) {
        fluidState.set(((WorldSliceExtension) (Object) slice).fluidlogged$getFluidState(x, y, z));
    }

    @Redirect(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
                    ordinal = 0
            )
    )
    private boolean redirectIsAir(BlockState instance, @Share("fluidState") LocalRef<FluidState> fluidState) {
        return instance.isAir() && fluidState.get().isEmpty();
    }

    @Redirect(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"
            )
    )
    private boolean redirectIsEmpty(FluidState instance, @Share("fluidState") LocalRef<FluidState> fluidState) {
        return fluidState.get().isEmpty();
    }

    @Inject(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderCache;getFluidRenderer()Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/FluidRenderer;"
            ),
            remap = false
    )
    private void modifyToActualFluidState(
            ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<ChunkBuildOutput> cir,
            @Local LocalRef<FluidState> fluidRef,
            @Share("fluidState") LocalRef<FluidState> actualFluidRef
    ) {
        FluidState fluid = fluidRef.get();
        fluidRef.set(fluid.isEmpty() ? actualFluidRef.get() : fluid);
    }

}
