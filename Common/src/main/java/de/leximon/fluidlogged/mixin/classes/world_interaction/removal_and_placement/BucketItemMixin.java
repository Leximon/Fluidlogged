package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Shadow @Final private Fluid content;
    @Shadow protected abstract void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos);

    @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
    @ModifyConstant(
            method = "use",
            constant = @Constant(ordinal = 0, classValue = LiquidBlockContainer.class)
    )
    private boolean redirectBypassLiquidBlockContainerCheck(Object reference, Class<LiquidBlockContainer> clazz) {
        return true;
    }

    @Redirect(
            method = "use",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;",
                    ordinal = 2,
                    opcode = Opcodes.GETFIELD
            )
    )
    private Fluid redirectBypassContentCheck(BucketItem instance) {
        return Fluids.WATER;
    }


    @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
    @ModifyConstant(
            method = "emptyContents",
            constant = @Constant(ordinal = 2, classValue = LiquidBlockContainer.class)
    )
    private boolean redirectBypassLiquidBlockContainerCheck2(Object reference, Class<LiquidBlockContainer> clazz) {
        return true;
    }


    @ModifyVariable(
            method = "emptyContents",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    private boolean modifyCanPlaceFluid(boolean bl2, @Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        BlockState blockState = level.getBlockState(blockPos);
        boolean replace = blockState.canBeReplaced(this.content);

        return blockState.isAir() || replace || Fluidlogged.canPlaceFluid(blockState);
    }


    @Inject(
            method = "emptyContents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void injectPlaceFluid(Player player, Level level, BlockPos blockPos, BlockHitResult blockHitResult, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(blockPos);

        Fluidlogged.placeFluid(level, blockPos, blockState, ((FlowingFluid) content).getSource(false));
        playEmptySound(player, level, blockPos);
        cir.setReturnValue(true);
    }
}
