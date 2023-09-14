package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedFabric;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FluidloggedFabricMixinPlugin implements IMixinConfigPlugin {

    private static final boolean SODIUM_LOADED = isModLoaded("sodium");
    private static final boolean LITHIUM_LOADED = isModLoaded("lithium");
    private static final boolean MILK_LIB_LOADED = isModLoaded("milk");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!SODIUM_LOADED && mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.fabric.compat_sodium"))
            return false;
        if (!LITHIUM_LOADED && mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.fabric.compat_lithium"))
            return false;
        if (!MILK_LIB_LOADED && mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.fabric.compat_milk_lib"))
            return false;
        return true;
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

    private static boolean isModLoaded(String name) {
        return FabricLoader.getInstance().isModLoaded(name);
    }
}
