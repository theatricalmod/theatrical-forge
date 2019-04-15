package dev.theatricalmod.theatrical.init;

import com.google.gson.Gson;
import dev.theatricalmod.theatrical.TheatricalMain;
import dev.theatricalmod.theatrical.api.fixtures.CustomFixture;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.fixtures.FixtureMovingHead;
import dev.theatricalmod.theatrical.util.Reference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class TheatricalFixtures {

    public static Gson gson = new Gson();

    //    public static Fixture FRESNEL = new FixtureFresnel();
    public static Fixture MOVING_HEAD = new FixtureMovingHead();
    public static List<Fixture> fixtures;

    public static void init() {
        fixtures = TheatricalFixtures.loadFromFiles(TheatricalMain.lightsDirectory);
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
                CustomFixture fixture = gson.fromJson(fileReader, CustomFixture.class);
                Fixture actualFixture = new Fixture(new ResourceLocation(Reference.MOD_ID, fixture.getName()), fixture.getFixtureType(), fixture.getHangableType(), new ResourceLocation(Reference.MOD_ID, fixture.getStaticModel()), new ResourceLocation(Reference.MOD_ID, fixture.getHookedModel()), new ResourceLocation(Reference.MOD_ID, fixture.getTiltModel()), new ResourceLocation(Reference.MOD_ID, fixture.getPanModel()), fixture.getTiltRotation(), fixture.getPanRotation(), fixture.getBeamStartPosition(), fixture.getDefaultRotation(), fixture.getBeamWidth(), fixture.getRayTraceRotation(), fixture.getMaxLightDistance(), fixture.getMaxEnergy(), fixture.getEnergyUse(), fixture.getEnergyUseTimer(), fixture.getChannelCount(), fixture.getChannelsDefinition());
                fixtures.add(actualFixture);
                TheatricalMain.logger.info("[Theatrical] Loaded light: " + fixture.getName());
            } catch (FileNotFoundException e) {
                TheatricalMain.logger.error("[Theatrical] Failed to load light with name " + lightFile.getName());
            }
        });
        return fixtures;
    }

}
