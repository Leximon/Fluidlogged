package de.leximon.fluidlogged.mixin;

import com.google.common.collect.ImmutableMap;
import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WallBlock.class)
public class WallBlockMixin {

//    @Redirect(method = "getShapeMap", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
//    private <S, V> ImmutableMap.Builder<BlockState, VoxelShape> injected(ImmutableMap.Builder<BlockState, VoxelShape> instance, S key, V value) {
//        for (int i = 0; i < FluidloggedConfig.fluids.size(); i++)
//            instance.put(((BlockState) key).with(FluidloggedMod.PROPERTY_FLUID, i), (VoxelShape) value);
//        return instance;
//    }



}
