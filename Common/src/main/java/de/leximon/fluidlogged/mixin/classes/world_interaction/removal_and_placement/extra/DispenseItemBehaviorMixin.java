package de.leximon.fluidlogged.mixin.classes.world_interaction.removal_and_placement.extra;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/core/dispenser/DispenseItemBehavior$17", priority = 950)
public abstract class DispenseItemBehaviorMixin extends DefaultDispenseItemBehavior {

    @Unique // forge doesn't like this as shadow for what ever reason
    private final DefaultDispenseItemBehavior fluidlogged$defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/dispenser/DefaultDispenseItemBehavior;execute(Lnet/minecraft/core/BlockSource;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            ),
            cancellable = true
    )
    private void injectRemovalOfFluidloggedBlocks(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        LevelAccessor level = blockSource.getLevel();
        BlockPos pos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
        FluidState fluidState = level.getFluidState(pos);

        if (fluidState.isEmpty() || !fluidState.isSource())
            return;


        ItemStack returnItemStack = fluidState.getType().getBucket().getDefaultInstance();
        if (returnItemStack.isEmpty())
            return;
        ((LevelExtension) level).setFluid(pos, Fluids.EMPTY.defaultFluidState(), Block.UPDATE_ALL);

        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        Item returnItem = returnItemStack.getItem();
        itemStack.shrink(1);

        if (itemStack.isEmpty()) {
            cir.setReturnValue(new ItemStack(returnItem));
            return;
        }

        if (blockSource.<DispenserBlockEntity>getEntity().addItem(new ItemStack(returnItem)) < 0)
            this.fluidlogged$defaultDispenseItemBehavior.dispense(blockSource, new ItemStack(returnItem));

        cir.setReturnValue(itemStack);
    }
    
}
