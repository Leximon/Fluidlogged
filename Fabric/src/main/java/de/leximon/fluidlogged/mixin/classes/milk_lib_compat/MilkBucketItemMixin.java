package de.leximon.fluidlogged.mixin.classes.milk_lib_compat;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import io.github.tropheusj.milk.Milk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "InvalidInjectorMethodSignature"})
@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin {

    @Shadow protected abstract void playEmptyingSound(@Nullable Player player, LevelAccessor world, BlockPos pos);

    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos redirectPlacementPos(BlockPos instance, Direction direction, Level level, Player user, InteractionHand hand) {
        boolean canPlace = Fluidlogged.canPlaceFluid(level, instance, level.getBlockState(instance), Milk.STILL_MILK);
        return canPlace ? instance : instance.relative(direction);
    }

    @ModifyVariable(
            method = "emptyContents",
            at = @At(value = "STORE"),
            ordinal = 1
    )
    private boolean modifyCanPlaceFluid(boolean bl2, @Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        BlockState blockState = level.getBlockState(blockPos);
        boolean replace = blockState.canBeReplaced(Milk.STILL_MILK);

        return blockState.isAir() || replace || Fluidlogged.canPlaceFluid(level, blockPos, blockState, Milk.STILL_MILK);
    }

    @ModifyConstant(
            method = "emptyContents",
            constant = @Constant(ordinal = 2, classValue = LiquidBlockContainer.class)
    )
    private boolean redirectBypassLiquidBlockContainerCheck(
            Object reference, Class<LiquidBlockContainer> clazz,
            @Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult hitResult
    ) {
        return Fluidlogged.canPlaceFluid(level, pos, level.getBlockState(pos), Milk.STILL_MILK);
    }

    @Inject(
            method = "emptyContents",
            at = @At(
                    value = "JUMP",
                    opcode = Opcodes.IFEQ,
                    ordinal = 4,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void injectFluidPlacement(Player player, Level level, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = Milk.STILL_MILK.getSource(false);

        // try to place the fluid via blockState first then via Fluidlogged
        if (!(blockState.getBlock() instanceof LiquidBlockContainer container && container.placeLiquid(level, pos, blockState, fluidState)))
            ((LevelExtension) level).setFluid(pos, fluidState, Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);

        playEmptyingSound(player, level, pos);
        cir.setReturnValue(true);
    }

}
