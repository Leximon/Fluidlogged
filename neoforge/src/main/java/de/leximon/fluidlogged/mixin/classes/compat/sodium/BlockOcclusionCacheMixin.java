package de.leximon.fluidlogged.mixin.classes.compat.sodium;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockOcclusionCache")
public class BlockOcclusionCacheMixin {

    @Redirect(
            method = "shouldDrawFullBlockFluidSide",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFluidState()Lnet/minecraft/world/level/material/FluidState;")
    )
    private FluidState shouldDrawFullBlockFluidSide(
            BlockState instance,
            @Local(argsOnly = true) BlockGetter view,
            @Local BlockPos.MutableBlockPos otherPos
    ) {
        return view.getFluidState(otherPos);
    }

    @ModifyExpressionValue(
            method = "shouldDrawFullBlockFluidSide",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformBlockAccess;shouldOccludeFluid(Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z")
    )
    private boolean shouldOccludeFluid(
            boolean original,
            @Local(argsOnly = true) BlockGetter view,
            @Local BlockPos.MutableBlockPos otherPos,
            @Local(argsOnly = true) FluidState fluid
    ) {
        if (!original) {
            return view.getFluidState(otherPos).getType().isSame(fluid.getType());
        }
        return true;
    }

}
