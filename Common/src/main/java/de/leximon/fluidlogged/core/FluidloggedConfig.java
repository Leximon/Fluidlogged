package de.leximon.fluidlogged.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leximon.fluidlogged.Constants;
import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FluidloggedConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final File CONFIG_FILE = new File(Services.PLATFORM.getConfigDir(), "fluidlogged.json");

    public static boolean compatibilityMode = false;
    public static List<ResourceLocation> fluids = new ArrayList<>();
    public static List<ResourceLocation> disabledEnforcedFluids = new ArrayList<>();

    public static List<ResourceLocation> fluidsLocked;
    public static List<ResourceLocation> enforcedFluids = new ArrayList<>();

    public static void init() {
        loadConfig();
        loadModConfigs();
        lockFluids();
    }

    private static void loadConfig() {
        if(!CONFIG_FILE.exists())
            saveConfig();

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);

            if(obj.has("compatibilityMode"))
                compatibilityMode = obj.get("compatibilityMode").getAsBoolean();
            if(obj.has("fluids")) {
                fluids.clear();
                JsonArray fluidArray = obj.getAsJsonArray("fluids");
                for (JsonElement element : fluidArray)
                    fluids.add(new ResourceLocation(element.getAsString()));
            }
            if(obj.has("disabledEnforcedFluids")) {
                disabledEnforcedFluids.clear();
                JsonArray fluidArray = obj.getAsJsonArray("disabledEnforcedFluids");
                for (JsonElement element : fluidArray)
                    disabledEnforcedFluids.add(new ResourceLocation(element.getAsString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        JsonObject obj = new JsonObject();
        obj.addProperty("compatibilityMode", compatibilityMode);

        {
            JsonArray fluidArray = new JsonArray();
            for (ResourceLocation fluid : fluids)
                fluidArray.add(fluid.toString());
            obj.add("fluids", fluidArray);
        }
        {
            JsonArray fluidArray = new JsonArray();
            for (ResourceLocation fluid : disabledEnforcedFluids)
                fluidArray.add(fluid.toString());
            obj.add("disabledEnforcedFluids", fluidArray);
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void lockFluids() {
        ArrayList<ResourceLocation> ids = new ArrayList<>(enforcedFluids.size() + fluids.size());
        ids.addAll(enforcedFluids);
        for (ResourceLocation id : fluids) // add all and prevent duplicates
            if(!ids.contains(id))
                ids.add(id);
        fluidsLocked = Collections.unmodifiableList(ids);
        Constants.LOGGER.info("Locked {} fluid(s)! Have fun :)", fluidsLocked.size());
    }

    private static void loadModConfigs() {
        Services.PLATFORM.loadModConfigs(FluidloggedConfig::loadModConfig);
        Constants.LOGGER.info("Enforced {} fluid(s)!", enforcedFluids.size());
    }

    public static void loadModConfig(Reader reader) {
        JsonObject obj = GSON.fromJson(reader, JsonObject.class);
        if(obj.has("fluids")) {
            JsonArray fluidArray = obj.getAsJsonArray("fluids");
            for (JsonElement element : fluidArray) {
                ResourceLocation id = new ResourceLocation(element.getAsString());
                if(!disabledEnforcedFluids.contains(id))
                    enforcedFluids.add(id);
            }
        }
    }

}
