package de.leximon.fluidlogged.core;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
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
    private static final File CONFIG_FILE;

    public static boolean compatibilityMode = false;
    public static List<Identifier> fluidsLocked;
    public static List<Identifier> fluids = new ArrayList<>();

    static {
        CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fluidlogged.json");
        fluids.add(new Identifier("minecraft", "lava")); // default
        loadConfig();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        JsonObject obj = new JsonObject();
        obj.addProperty("compatibilityMode", compatibilityMode);

        JsonArray fluidArray = new JsonArray();
        for (Identifier fluid : fluids)
            fluidArray.add(fluid.toString());
        obj.add("fluids", fluidArray);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(obj, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void lockFluids() {
        ArrayList<Identifier> ids = new ArrayList<>(fluids);
        fluidsLocked = Collections.unmodifiableList(ids);
    }

}
