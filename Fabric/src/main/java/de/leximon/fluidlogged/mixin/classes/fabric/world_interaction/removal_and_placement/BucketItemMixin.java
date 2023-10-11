package de.leximon.fluidlogged.mixin.classes.fabric.world_interaction.removal_and_placement;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {

    @Shadow @Final private Fluid content;

    @Shadow protected abstract void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos);

    public BucketItemMixin(Properties properties) {
        super(properties);
    }


    @SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
    @ModifyConstant(
            method = "use",
            constant = @Constant(ordinal = 0, classValue = LiquidBlockContainer.class)
    )
    private boolean redirectBypassLiquidBlockContainerCheck(Object reference, Class<LiquidBlockContainer> clazz) {
        return true;
    }

    @Unique private BlockPos fluidlogged$blockPos;
    @Unique private BlockState fluidlogged$blockState;

    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectCaptureLocals(
            Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
            ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState
    ) {
        this.fluidlogged$blockPos = blockPos;
        this.fluidlogged$blockState = blockState;
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
    private Fluid redirectBypassContentCheck(BucketItem instance, Level level, Player player, InteractionHand interactionHand) {
        return Fluidlogged.canPlaceFluid(level, this.fluidlogged$blockPos, this.fluidlogged$blockState, this.content)
                ? Fluids.WATER
                : null;
    }


    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void injectRemoveFluid(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState) {
        if (blockState instanceof BucketPickup)
            return; // let the code after this injection handle it

        FluidState fluidState = level.getFluidState(blockPos);
        if (fluidState == blockState.getFluidState() || fluidState.isEmpty())
            return;

        if (!fluidState.isSource())
            return;

        Fluid fluid = fluidState.getType();
        ItemStack filledBucket = fluid.getBucket().getDefaultInstance();

        player.awardStat(Stats.ITEM_USED.get(this));
        fluid.getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
        level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
        ItemStack newFilledBucket = ItemUtils.createFilledResult(itemStack, player, filledBucket);
        if (!level.isClientSide)
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, filledBucket);

        ((LevelExtension) level).setFluid(blockPos, Fluids.EMPTY.defaultFluidState(), Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);

        cir.setReturnValue(InteractionResultHolder.sidedSuccess(newFilledBucket, level.isClientSide()));
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

        return blockState.isAir() || replace || Fluidlogged.canPlaceFluid(level, blockPos, blockState, this.content);
    }


    @Redirect(
            method = "emptyContents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/BucketItem;content:Lnet/minecraft/world/level/material/Fluid;",
                    ordinal = 4,
                    opcode = Opcodes.GETFIELD
            )
    )
    private Fluid redirectBypassContentCheck2(BucketItem instance, @Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        BlockState blockState = level.getBlockState(blockPos);

        // place the fluid normally if the block is not waterloggable/fluidloggable
        return Fluidlogged.canPlaceFluid(level, blockPos, blockState, this.content) ? Fluids.WATER : null;
    }


    @Inject(
            method = "emptyContents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/material/Fluids;WATER:Lnet/minecraft/world/level/material/FlowingFluid;",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = 2
            ),
            cancellable = true
    )
    private void injectPlaceFluid(Player player, Level level, BlockPos blockPos, BlockHitResult blockHitResult, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(blockPos);
        FluidState contentFluidState = ((FlowingFluid) this.content).getSource(false);

        // try to place the fluid via blockState first then via Fluidlogged
        if (blockState.getBlock() instanceof LiquidBlockContainer container && container.placeLiquid(level, blockPos, blockState, contentFluidState)) {
            ((LevelExtension) level).setFluid(blockPos, Fluids.EMPTY.defaultFluidState(), Block.UPDATE_ALL);
        } else {
            if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
                BlockState newBlockState = blockState.setValue(BlockStateProperties.WATERLOGGED, false);
                level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
            }
            ((LevelExtension) level).setFluid(blockPos, contentFluidState, Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
        }

        playEmptySound(player, level, blockPos);
        cir.setReturnValue(true);
    }
}
