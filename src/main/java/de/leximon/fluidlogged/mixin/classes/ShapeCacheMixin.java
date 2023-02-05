package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("deprecation")
@Mixin(targets = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase$Cache")
public class ShapeCacheMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injectCustomFluidCollisionShape(Block instance, BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return instance.getCollisionShape(
                state.hasProperty(Fluidlogged.PROPERTY_FLUID)
                        ? state.setValue(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }
}
