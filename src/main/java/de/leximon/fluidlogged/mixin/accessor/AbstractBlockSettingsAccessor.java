package de.leximon.fluidlogged.mixin.accessor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.ToIntFunction;

@Mixin(BlockBehaviour.Properties.class)
public interface AbstractBlockSettingsAccessor
{
    @Accessor("lightEmission")
    ToIntFunction<BlockState> fluidlogged_getLuminance();
}
