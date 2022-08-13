package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.MissingVoxelShapeException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviourMixin {

    @Redirect(method = "isSolidRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getOcclusionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape inject(BlockState instance, BlockGetter blockGetter, BlockPos blockPos) {
        VoxelShape shape = instance.getCollisionShape(blockGetter, blockPos);
        if(shape == null) {
            FluidloggedMod.LOGGER.error("VoxelShape of {} cannot be null!", instance);
            throw new MissingVoxelShapeException("VoxelShape cannot be null!");
        }
        return shape;
    }

}
