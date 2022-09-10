package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin<O, S extends State<O, S>> {

    @Shadow @Final private O owner;

    @Inject(method = "add", at = @At("HEAD"))
    private void injectFluidloggedProperty(Property<?>[] properties, CallbackInfoReturnable<StateManager.Builder<O, S>> cir) {
        if(!FluidloggedConfig.compatibilityMode || Fluidlogged.isVanillaWaterloggable(owner)) {
            for (Property<?> property : properties) {
                if (property.getName().equals("waterlogged")) {
                    add(Fluidlogged.PROPERTY_FLUID);
                    break;
                }
            }
        }
    }

    @Shadow public abstract StateManager.Builder<O, S> add(Property<?>... properties);
}