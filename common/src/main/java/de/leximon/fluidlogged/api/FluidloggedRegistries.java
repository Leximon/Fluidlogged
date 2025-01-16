package de.leximon.fluidlogged.api;

import de.leximon.fluidlogged.config.Addon;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

public class FluidloggedRegistries {

    public static final Object2ObjectMap<ResourceLocation, Addon> ADDONS = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());


    public static <T> T register(Object2ObjectMap<ResourceLocation, T> registry, ResourceLocation id, T value) {
        if (registry.containsKey(id))
            throw new IllegalArgumentException("Duplicate id: " + id);

        registry.put(id, value);
        return value;
    }

    public static <T> T get(Object2ObjectMap<ResourceLocation, T> registry, ResourceLocation id) {
        return registry.get(id);
    }
}
