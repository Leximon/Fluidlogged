package de.leximon.fluidlogged.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.leximon.fluidlogged.api.FluidloggedRegistries;
import de.leximon.fluidlogged.platform.services.Services;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@DefaultQualifier(NonNull.class)
public class Config {

    public static final String CONFIG_FILE_NAME = "fluidlogged.json";
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Object2BooleanMap<ResourceLocation> addons = new Object2BooleanOpenHashMap<>();

    final BlockPredicateList fluidloggableBlocks = new BlockPredicateList(this, Addon::fluidloggableBlocks);
    boolean fluidPermeabilityEnabled = true;
    final BlockPredicateList fluidPermeableBlocks = new BlockPredicateList(this, Addon::fluidPermeableBlocks);
    final BlockPredicateList shapeIndependentFluidPermeableBlocks = new BlockPredicateList(this, Addon::shapeIndependentFluidPermeableBlocks);

    public boolean isFluidloggable(BlockState block) {
        return this.fluidloggableBlocks.contains(block);
    }

    public boolean isFluidPermeable(BlockState block) {
        return this.fluidPermeableBlocks.contains(block);
    }

    public boolean isShapeIndependentFluidPermeable(BlockState block) {
        return this.shapeIndependentFluidPermeableBlocks.contains(block);
    }

    public List<Addon> getEnabledAddons() {
        List<Addon> enabledAddons = new ArrayList<>(this.addons.size());

        for (Map.Entry<ResourceLocation, Addon> addonEntry : FluidloggedRegistries.ADDONS.entrySet()) {
            ResourceLocation addonId = addonEntry.getKey();
            Addon addon = addonEntry.getValue();

            boolean enabled = this.addons.getBoolean(addonId) || (!this.addons.containsKey(addonId) && addon.enabledByDefault());
            if (!enabled)
                continue;

            enabledAddons.add(addon);
        }

        return enabledAddons;
    }

    public void compile() {
        this.fluidloggableBlocks.compile();
        this.fluidPermeableBlocks.compile();
        this.shapeIndependentFluidPermeableBlocks.compile();
    }

    public void save() {
        File file = Services.PLATFORM.getConfigFile();

        JsonObject obj = new JsonObject();

        JsonObject addonsObj = new JsonObject();
        for (Object2BooleanMap.Entry<ResourceLocation> entry : this.addons.object2BooleanEntrySet()) {
            String id = entry.getKey().toString();
            boolean enabled = entry.getBooleanValue();
            addonsObj.addProperty(id, enabled);
        }
        obj.add("addons", addonsObj);

        obj.addProperty("fluid_permeability_enabled", this.fluidPermeabilityEnabled);
        obj.add("fluidloggable_blocks", this.fluidloggableBlocks.toJson());
        obj.add("fluid_permeable_blocks", this.fluidPermeableBlocks.toJson());
        obj.add("shape_independent_fluid_permeable_blocks", this.shapeIndependentFluidPermeableBlocks.toJson());

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config " + CONFIG_FILE_NAME, e);
        }
    }

    public void load() {
        File file = Services.PLATFORM.getConfigFile();
        if (!file.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            if (obj.has("addons")) {
                JsonObject addonsObj = obj.getAsJsonObject("addons");
                for (String id : addonsObj.keySet()) {
                    ResourceLocation addonId = new ResourceLocation(id);
                    boolean enabled = addonsObj.get(id).getAsBoolean();
                    this.addons.put(addonId, enabled);
                }
            }

            if (obj.has("fluid_permeability_enabled"))
                this.fluidPermeabilityEnabled = obj.get("fluid_permeability_enabled").getAsBoolean();

            if (obj.has("fluidloggable_blocks") && obj.get("fluidloggable_blocks").isJsonObject())
                this.fluidloggableBlocks.fromJson(obj.getAsJsonObject("fluidloggable_blocks"));

            if (obj.has("fluid_permeable_blocks"))
                this.fluidPermeableBlocks.fromJson(obj.getAsJsonObject("fluid_permeable_blocks"));

            if (obj.has("shape_independent_fluid_permeable_blocks"))
                this.shapeIndependentFluidPermeableBlocks.fromJson(obj.getAsJsonObject("shape_independent_fluid_permeable_blocks"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config " + CONFIG_FILE_NAME, e);
        }
    }
}
