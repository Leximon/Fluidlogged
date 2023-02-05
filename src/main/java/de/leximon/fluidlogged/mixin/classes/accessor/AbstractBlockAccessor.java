package de.leximon.fluidlogged.mixin.classes.accessor;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface AbstractBlockAccessor {

    @Accessor("properties")
    BlockBehaviour.Properties fl_getSettings();

}
