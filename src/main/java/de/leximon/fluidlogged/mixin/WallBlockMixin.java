package de.leximon.fluidlogged.mixin;

import com.google.common.collect.ImmutableMap;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WallBlock.class)
public class WallBlockMixin
{
    @Redirect(method = "makeShapes", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    private <S, V> ImmutableMap.Builder<BlockState, VoxelShape> injected(ImmutableMap.Builder<BlockState, VoxelShape> instance, S key, V value)
    {
        if(FluidloggedConfig.getCompatibilityMode() && Fluidlogged.isVanillaWaterloggable(this))
        {
            for (int i = 0; i < FluidloggedConfig.getFluidList().size() + 1; i++)
                instance.put(((BlockState) key).setValue(Fluidlogged.PROPERTY_FLUID, i), (VoxelShape) value);
            return instance;
        }
        return instance.put((BlockState) key, (VoxelShape) value);
    }
}
