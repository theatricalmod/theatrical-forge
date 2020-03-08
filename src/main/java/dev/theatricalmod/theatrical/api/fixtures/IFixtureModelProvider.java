package dev.theatricalmod.theatrical.api.fixtures;


import net.minecraft.util.ResourceLocation;

public interface IFixtureModelProvider {

    HangableType getHangType();

    ResourceLocation getStaticModel();

    ResourceLocation getTiltModel();

    ResourceLocation getPanModel();

    float[] getTiltRotationPosition();

    float[] getPanRotationPosition();

    float getDefaultRotation();

    float[] getBeamStartPosition();

    float getBeamWidth();

    float getRayTraceRotation();
}
