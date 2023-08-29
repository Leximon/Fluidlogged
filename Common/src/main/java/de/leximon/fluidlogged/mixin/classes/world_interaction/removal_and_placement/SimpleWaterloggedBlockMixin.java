package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement;

import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWaterloggedBlock.class)
public interface SimpleWaterloggedBlockMixin {

    @Inject(method = "canPlaceLiquid", at = @At("HEAD"), cancellable = true)
    default void canPlaceLiquid(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true); // allow any fluid to be placed, can be overridden to prevent this for specific blockStates e.g. SlabBlock
    }

}
