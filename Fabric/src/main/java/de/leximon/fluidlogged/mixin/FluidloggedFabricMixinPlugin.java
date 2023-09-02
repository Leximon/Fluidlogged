package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedFabric;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FluidloggedFabricMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return (FluidloggedFabric.SODIUM_LOADED || !mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.rendering.sodium_compat"))
                && (FluidloggedFabric.LITHIUM_LOADED || !mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.rendering.lithium_compat"))
                && (FluidloggedFabric.MILK_LIB_LOADED || !mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.rendering.milk_lib_compat"));
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
