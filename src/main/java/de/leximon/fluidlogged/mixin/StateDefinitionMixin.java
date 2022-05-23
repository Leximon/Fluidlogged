package de.leximon.fluidlogged.mixin;

import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.regex.Pattern;

@Mixin(StateDefinition.class)
public class StateDefinitionMixin
{
    @Final
    @Shadow
    static Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_:]*?$");
}
