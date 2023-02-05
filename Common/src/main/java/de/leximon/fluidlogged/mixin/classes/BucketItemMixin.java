package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.FluidloggedCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(value = BucketItem.class)
public class BucketItemMixin {

    // allow any fluid to fluidlog a block
    @Redirect(method = "use", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;", opcode = Opcodes.GETFIELD, ordinal = 2))
    private Fluid unblock(BucketItem instance) {
        return Fluids.WATER;
    }

    @Redirect(
            method = "emptyContents",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;", opcode = Opcodes.GETFIELD, ordinal = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlockContainer;placeLiquid(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z")
            )
    )
    private Fluid unblock2(BucketItem instance) {
        return Fluids.WATER;
    }


    // play the right sound when draining a fluidlogged block
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;getPickupSound()Ljava/util/Optional;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void playRightSound(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState, BucketPickup fluidDrainable, ItemStack itemStack2) {
        Fluid fluid = FluidloggedCommon.getFluid(blockState);
        if(fluid != null) {
            fluid.getPickupSound().ifPresentOrElse(
                    sound -> user.playSound(sound, 1.0F, 1.0F),
                    () -> user.playSound(Fluids.WATER.getPickupSound().orElseThrow(), 1.0F, 1.0F)
            );
        }
    }
}
