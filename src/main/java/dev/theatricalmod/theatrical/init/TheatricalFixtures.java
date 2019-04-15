package dev.theatricalmod.theatrical.init;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.theatricalmod.theatrical.TheatricalMain;
import dev.theatricalmod.theatrical.api.fixtures.CustomFixture;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.fixtures.FixtureMovingHead;
import dev.theatricalmod.theatrical.util.Reference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class TheatricalFixtures {

    public static Gson gson = new Gson();

    //    public static Fixture FRESNEL = new FixtureFresnel();
    public static Fixture MOVING_HEAD = new FixtureMovingHead();
    public static List<Fixture> fixtures;

    public static void init() {
        fixtures = new ArrayList<>();
        List<String> files = new ArrayList<>();
        try {
            for (IResource resource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(Reference.MOD_ID, "lights.json"))) {
                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(new InputStreamReader(resource.getInputStream())).getAsJsonObject();
                resource.getInputStream().close();
                if (object.has("lights")) {
                    JsonArray array = object.getAsJsonArray("lights");
                    array.forEach(jsonElement -> files.add(jsonElement.getAsString()));
                }
            }
            files.forEach(s -> {
                try {
                    for (IResource resource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(Reference.MOD_ID, "lights/" + s))) {
                        Fixture fixture = fromJson(new InputStreamReader(resource.getInputStream()));
                        fixtures.add(fixture);
                        TheatricalMain.instance.logger.info("[Theatrical] Loaded light: " + fixture.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    TheatricalMain.instance.logger.error("[Theatrical] Failed to load light with name " + s);
                }
            });
        } catch (IOException e) {
            TheatricalMain.instance.logger.error("An error occurred whilst loading lights.");
        }
    }

    public static void initServer() {
        fixtures = TheatricalFixtures.loadFromFiles(TheatricalMain.instance.lightsDirectory);
    }

    public static Fixture fromJson(Reader json) {
        CustomFixture fixture = gson.fromJson(json, CustomFixture.class);
        Fixture actualFixture = new Fixture(new ResourceLocation(Reference.MOD_ID, fixture.getName()), fixture.getFixtureType(), fixture.getHangableType(), new ResourceLocation(Reference.MOD_ID, fixture.getStaticModel()), new ResourceLocation(Reference.MOD_ID, fixture.getHookedModel()), new ResourceLocation(Reference.MOD_ID, fixture.getTiltModel()), new ResourceLocation(Reference.MOD_ID, fixture.getPanModel()), fixture.getTiltRotation(), fixture.getPanRotation(), fixture.getBeamStartPosition(), fixture.getDefaultRotation(), fixture.getBeamWidth(), fixture.getRayTraceRotation(), fixture.getMaxLightDistance(), fixture.getMaxEnergy(), fixture.getEnergyUse(), fixture.getEnergyUseTimer(), fixture.getChannelCount(), fixture.getChannelsDefinition());
        return actualFixture;
    }

    public static List<Fixture> loadFromFiles(File configDir) {
        List<Fixture> fixtures = new ArrayList<>();
        List<File> fileList = Arrays.asList(configDir.listFiles());
        fileList.forEach(lightFile -> {
            if (!lightFile.getName().endsWith(".json")) {
                return;
            }
            try {
                FileReader fileReader = new FileReader(lightFile);
                Fixture fixture = fromJson(fileReader);
                fixtures.add(fixture);
                TheatricalMain.instance.logger.info("[Theatrical] Loaded light: " + fixture.getName());
            } catch (FileNotFoundException e) {
                TheatricalMain.instance.logger.error("[Theatrical] Failed to load light with name " + lightFile.getName());
            }
        });
        return fixtures;
    }

}
