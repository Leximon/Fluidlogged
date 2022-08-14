package de.leximon.fluidlogged.mixin.accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.ToIntFunction;

@Mixin(AbstractBlock.Settings.class)
public interface AbstractBlockSettingsAccessor {

    @Accessor("luminance")
    ToIntFunction<BlockState> fl_getLuminance();

}
