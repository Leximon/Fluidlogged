package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin<O, S extends State<O, S>> {

    @Shadow public abstract StateManager.Builder<O, S> add(Property<?>... properties);

    @Inject(method = "add", at = @At("HEAD"))
    private void injectFluidloggedProperty(Property<?>[] properties, CallbackInfoReturnable<StateManager.Builder<O, S>> cir) {
        for (Property<?> property : properties) {
            if (property.getName().equals("waterlogged")) {
                add(FluidloggedMod.PROPERTY_FLUID);
                break;
            }
        }
    }
}