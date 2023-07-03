package net.minestom.generators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minestom.datagen.DataGenerator;
import net.minestom.utils.ResourceUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class BiomeGenerator extends DataGenerator {

    private static final String BIOMES_DIR = "data/minecraft/worldgen/biome/";
    private static final Gson gson = new Gson();

    @Override
    public JsonObject generate() throws Exception {
        var resultJson = new JsonObject();
        var biomes = readBiomes();

        for (var entry : biomes.entrySet()) {
            var biomeJson = entry.getValue();
            JsonObject mappedJson = new JsonObject();

            mappedJson.add("precipitation", biomeJson.get("precipitation"));
            mappedJson.add("temperature", biomeJson.get("temperature"));
            if (biomeJson.has("temperature_modifier")) {
                mappedJson.addProperty("temperatureModifier", biomeJson.get("temperature_modifier").getAsString().toUpperCase());
            }
            mappedJson.add("downfall", biomeJson.get("downfall"));

            JsonObject effectsJson = biomeJson.getAsJsonObject("effects");
            JsonObject mappedEffectsJson = new JsonObject();
            mappedEffectsJson.add("fogColor", effectsJson.get("fog_color"));
            mappedEffectsJson.add("waterColor", effectsJson.get("water_color"));
            mappedEffectsJson.add("waterFogColor", effectsJson.get("water_fog_color"));
            mappedEffectsJson.add("skyColor", effectsJson.get("sky_color"));
            if (effectsJson.has("foliageColor")) {
                mappedEffectsJson.add("foliageColor", effectsJson.get("foliage_color"));
            }
            if (effectsJson.has("grassColor")) {
                mappedEffectsJson.add("grassColor", effectsJson.get("grass_color"));
            }
            if (effectsJson.has("grassColorModifier")) {
                mappedEffectsJson.addProperty("grass_colorModifier", effectsJson.get("grass_color_modifier").getAsString().toUpperCase());
            }
            if (effectsJson.has("particle")) {
                mappedEffectsJson.add("particle", effectsJson.get("particle"));
            }
            if (effectsJson.has("ambientSound")) {
                mappedEffectsJson.add("ambientSound", effectsJson.get("ambient_sound"));
            }
            if (effectsJson.has("moodSound")) {
                mappedEffectsJson.add("moodSound", effectsJson.get("mood_sound"));
            }
            if (effectsJson.has("additionsSound")) {
                mappedEffectsJson.add("additionsSound", effectsJson.get("additions_sound"));
            }
            if (effectsJson.has("music")) {
                mappedEffectsJson.add("music", effectsJson.get("music"));
            }

            mappedJson.add("effects", effectsJson);
            resultJson.add(entry.getKey(), mappedJson);
        }

        return resultJson;
    }

    private Map<String, JsonObject> readBiomes() throws URISyntaxException, IOException {
        // get all files from the biomes directory
        var files = ResourceUtils.getResourceListing(
                net.minecraft.server.MinecraftServer.class, BIOMES_DIR);

        Map<String, JsonObject> biomesJson = new HashMap<>();
        for (String fileName : files) {
            var file = net.minecraft.server.MinecraftServer.class
                    .getClassLoader()
                    .getResourceAsStream(BIOMES_DIR + fileName);
            var scanner = new Scanner(file);
            var content = new StringBuilder();
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
            }
            scanner.close();

            // only collect valid biome files
            if (content.length() > 0 && fileName.endsWith(".json")) {
                var biomeKey = "minecraft:" + fileName.substring(0, fileName.length() - 5);
                var jsonObject = gson.fromJson(content.toString(), JsonObject.class);
                biomesJson.put(biomeKey, jsonObject);
            }
        }

        return biomesJson;
    }

}
