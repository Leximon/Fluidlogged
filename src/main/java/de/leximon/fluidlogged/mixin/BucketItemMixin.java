package de.leximon.fluidlogged.mixin;


import de.leximon.fluidlogged.Fluidlogged;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BucketItem.class)
public class BucketItemMixin
{
    // play the right sound when draining a fluidlogged block
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;getPickupSound(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void injectSound(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemstack, BlockHitResult blockhitresult, InteractionResultHolder ret, BlockPos blockpos, Direction direction, BlockPos blockpos2, BlockState blockstate, BucketPickup bucketpickup, ItemStack itemstack2) {
        Fluid fluid = Fluidlogged.getFluid(blockstate);
        if(fluid != null) {
            fluid.getPickupSound().ifPresentOrElse(
                    sound -> player.playSound(sound, 1.0F, 1.0F),
                    () -> player.playSound(Fluids.WATER.getPickupSound().orElseThrow(), 1.0F, 1.0F)
            );
        }
    }
}
