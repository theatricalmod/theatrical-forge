package dev.theatricalmod.theatrical.api.fixtures;

import net.minecraft.client.renderer.block.model.IBakedModel;

public interface IFixtureModelProvider {

    HangableType getHangType();

    IBakedModel getStaticModel();

    IBakedModel getTiltModel();

    IBakedModel getPanModel();

    float[] getTiltRotationPosition();

    float[] getPanRotationPosition();

    float getDefaultRotation();

    float[] getBeamStartPosition();

    float getBeamWidth();

    float getRayTraceRotation();
}
