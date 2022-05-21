package de.leximon.fluidlogged.mixin;
import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionBuilderMixin <O, S extends StateHolder<O, S>> {

    @Shadow
    public abstract StateDefinition.Builder<O, S> add(Property<?>... properties);

    @Shadow @Final
    private O owner;

    @Inject(method = "add", at = @At("HEAD"))
    private void injectFluidloggedProperty(Property<?>[] properties, CallbackInfoReturnable<StateDefinition.Builder<O, S>> cir)
    {
        for (Property<?> property : properties)
        {
            if (property.getName().equals("waterlogged"))
            {
                add(Fluidlogged.PROPERTY_FLUID);
                break;
            }
        }
    }
}
