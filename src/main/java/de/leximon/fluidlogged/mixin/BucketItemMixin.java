package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
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
    @Redirect(method = "use", at = @At(value = "FIELD", target = "Lnet/minecraft/item/BucketItem;fluid:Lnet/minecraft/fluid/Fluid;", opcode = Opcodes.GETFIELD, ordinal = 2))
    private Fluid unblock(BucketItem instance) {
        return Fluids.WATER;
    }

    @Redirect(
            method = "placeFluid",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/BucketItem;fluid:Lnet/minecraft/fluid/Fluid;", opcode = Opcodes.GETFIELD, ordinal = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidFillable;tryFillWithFluid(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Z")
            )
    )
    private Fluid unblock2(BucketItem instance) {
        return Fluids.WATER;
    }


    // play the right sound when draining a fluidlogged block
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidDrainable;getBucketFillSound()Ljava/util/Optional;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void playRightSound(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState, FluidDrainable fluidDrainable, ItemStack itemStack2) {
        Fluid fluid = FluidloggedMod.getFluid(blockState);
        if(fluid != null) {
            fluid.getBucketFillSound().ifPresentOrElse(
                    sound -> user.playSound(sound, 1.0F, 1.0F),
                    () -> user.playSound(Fluids.WATER.getBucketFillSound().orElseThrow(), 1.0F, 1.0F)
            );
        }
    }
}
