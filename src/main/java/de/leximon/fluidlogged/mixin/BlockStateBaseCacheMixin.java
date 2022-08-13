package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase$Cache")
public class BlockStateBaseCacheMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape injected(Block instance, BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return instance.getCollisionShape(
                blockState.hasProperty(FluidloggedMod.PROPERTY_FLUID)
                        ? blockState.setValue(FluidloggedMod.PROPERTY_FLUID, 0)
                        : blockState,
                blockGetter, blockPos, collisionContext
        );
    }
}
