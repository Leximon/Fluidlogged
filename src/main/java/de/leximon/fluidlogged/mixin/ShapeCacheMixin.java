package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.FluidloggedConfig;
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
        if (state.contains(FluidloggedMod.PROPERTY_FLUID)) {
//            VoxelShape shape = null;
//            int i = 0;
//            while (shape == null && i < FluidloggedConfig.fluids.size() + 1) {
//                shape = instance.getCollisionShape(state.with(FluidloggedMod.PROPERTY_FLUID, i), world, pos, context);
//                i++;
//            }
            return instance.getCollisionShape(state.with(FluidloggedMod.PROPERTY_FLUID, 0), world, pos, context);
        } else
            return instance.getCollisionShape(state, world, pos, context);
    }
}
