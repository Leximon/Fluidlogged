package de.leximon.fluidlogged.mixin.classes.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.ToIntFunction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.Properties.class)
public interface AbstractBlockSettingsAccessor {

    @Accessor("lightEmission")
    ToIntFunction<BlockState> fl_getLuminance();

}
