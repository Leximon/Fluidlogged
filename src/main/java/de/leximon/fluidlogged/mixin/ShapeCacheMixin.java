package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/block/AbstractBlock$AbstractBlockState$ShapeCache")
public class ShapeCacheMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injected(Block instance, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return instance.getCollisionShape(
                state.contains(FluidloggedMod.PROPERTY_FLUID)
                        ? state.with(FluidloggedMod.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }
}
