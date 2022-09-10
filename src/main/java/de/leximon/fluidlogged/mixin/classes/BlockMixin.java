package de.leximon.fluidlogged.mixin.classes;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class BlockMixin {

    @Shadow private BlockState defaultState;

    @Redirect(method = "setDefaultState", at = @At(value = "FIELD", target = "Lnet/minecraft/block/Block;defaultState:Lnet/minecraft/block/BlockState;", opcode = Opcodes.PUTFIELD))
    private void injectDefaultState(Block instance, BlockState value) {
        if(FluidloggedConfig.compatibilityMode)
            defaultState = Fluidlogged.isVanillaWaterloggable(instance) && value.contains(Properties.WATERLOGGED)
                    ? value.with(Fluidlogged.PROPERTY_FLUID, 0)
                    : value;
        else defaultState = value.contains(Properties.WATERLOGGED) ? value.with(Fluidlogged.PROPERTY_FLUID, 0) : value;
    }

}
