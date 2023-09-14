package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedForge;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FluidloggedForgeMixinPlugin implements IMixinConfigPlugin {

    public static final boolean RUBIDIUM_LOADED = isModLoaded("rubidium") || isModLoaded("embeddium");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!RUBIDIUM_LOADED && mixinClassName.startsWith("de.leximon.fluidlogged.mixin.classes.compat_sodium"))
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
        return FMLLoader.getLoadingModList().getModFileById("rubidium") != null;
    }
}
