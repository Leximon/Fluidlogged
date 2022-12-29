package de.leximon.fluidlogged.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leximon.fluidlogged.Fluidlogged;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FluidloggedConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fluidlogged.json");

    public static boolean compatibilityMode = false;
    public static List<Identifier> fluids = new ArrayList<>();
    public static List<Identifier> disabledEnforcedFluids = new ArrayList<>();

    public static List<Identifier> fluidsLocked;
    public static List<Identifier> enforcedFluids = new ArrayList<>();

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
                    fluids.add(new Identifier(element.getAsString()));
            }
            if(obj.has("disabledEnforcedFluids")) {
                disabledEnforcedFluids.clear();
                JsonArray fluidArray = obj.getAsJsonArray("disabledEnforcedFluids");
                for (JsonElement element : fluidArray)
                    disabledEnforcedFluids.add(new Identifier(element.getAsString()));
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
            for (Identifier fluid : fluids)
                fluidArray.add(fluid.toString());
            obj.add("fluids", fluidArray);
        }
        {
            JsonArray fluidArray = new JsonArray();
            for (Identifier fluid : disabledEnforcedFluids)
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
        ArrayList<Identifier> ids = new ArrayList<>(enforcedFluids.size() + fluids.size());
        ids.addAll(enforcedFluids);
        for (Identifier id : fluids) // add all and prevent duplicates
            if(!ids.contains(id))
                ids.add(id);
        fluidsLocked = Collections.unmodifiableList(ids);
        Fluidlogged.LOGGER.info("Locked {} fluid(s)! Have fun :)", fluidsLocked.size());
    }

    private static void loadModConfigs() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            CustomValue value = mod.getMetadata().getCustomValue("fluidlogged");
            if(value == null)
                continue;
            Optional<Path> confPath = mod.findPath(value.getAsString());
            confPath.ifPresent(FluidloggedConfig::loadModConfig);
        }
        Fluidlogged.LOGGER.info("Enforced {} fluid(s)!", enforcedFluids.size());
    }

    private static void loadModConfig(Path path) {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if(obj.has("fluids")) {
                JsonArray fluidArray = obj.getAsJsonArray("fluids");
                for (JsonElement element : fluidArray) {
                    Identifier id = new Identifier(element.getAsString());
                    if(!disabledEnforcedFluids.contains(id))
                        enforcedFluids.add(id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
