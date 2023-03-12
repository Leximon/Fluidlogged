package de.leximon.fluidlogged.mixin.classes.compat.create;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import de.leximon.fluidlogged.FluidloggedCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Contraption.class)
public class ContraptionMixin {


    @Group(name = "removeBlocksFromWorld", min = 3)
    @Redirect(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z", ordinal = 0), require = 0)
    private boolean redirectHasProperty(BlockState state, Property<?> property) {
        return state.hasProperty(property) || state.hasProperty(FluidloggedCommon.PROPERTY_FLUID);
    }

    @Group(name = "removeBlocksFromWorld", min = 3)
    @Redirect(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0), require = 0)
    private Comparable<Boolean> redirectGetValue(BlockState state, Property<Boolean> property) {
        if (state.hasProperty(FluidloggedCommon.PROPERTY_FLUID))
            return state.getValue(property) || state.getValue(FluidloggedCommon.PROPERTY_FLUID) != 0;
        return state.getValue(property);
    }

    @Group(name = "removeBlocksFromWorld", min = 3)
    @Redirect(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), require = 0)
    private boolean redirectSetBlock(Level instance, BlockPos blockPos, BlockState blockState, int i) {
        BlockState oldState = instance.getBlockState(blockPos);
        if (oldState.hasProperty(BlockStateProperties.WATERLOGGED) && oldState.getValue(BlockStateProperties.WATERLOGGED))
            return instance.setBlock(blockPos, blockState, i);
        Fluid fluid = FluidloggedCommon.getFluid(oldState);
        if (fluid == null)
            return false;
        return instance.setBlock(blockPos, FluidloggedCommon.fluidBlocks.get(fluid).defaultBlockState(), i);
    }

    @Redirect(method = "addBlocksToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), require = 0)
    private boolean redirectSetBlock2(Level instance, BlockPos blockPos, BlockState blockState, int i) {
        Fluid fluid = instance.getFluidState(blockPos).getType();
        if (fluid == Fluids.WATER)
            return instance.setBlock(blockPos, blockState, i);
        return instance.setBlock(blockPos, blockState.setValue(FluidloggedCommon.PROPERTY_FLUID, FluidloggedCommon.getFluidIndex(fluid)), i);
    }

}
