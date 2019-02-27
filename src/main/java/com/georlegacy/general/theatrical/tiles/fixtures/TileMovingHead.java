/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.tiles.fixtures;

import com.georlegacy.general.theatrical.api.HangableType;
import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockMovingHead;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.tiles.TileRGBFixture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class TileMovingHead extends TileRGBFixture  {

    private int channelStartPoint = 0;

    /**
     * 0 - Intensity
     * 1 - Focus
     * 2 - Pan
     * 3 - Tilt
     * 4 - Red
     * 5 - Green
     * 6 - Blue
     */

    public TileMovingHead() {
        super(7, 0);
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockMovingHead.class;
    }

    @Override
    public float getMaxLightDistance() {
        return 7;
    }

    @Override
    public HangableType getHangType() {
        return HangableType.BRACE_BAR;
    }

    @Override
    public IBakedModel getStaticModel() {
        return TheatricalModels.MOVING_HEAD_STATIC;
    }

    @Override
    public IBakedModel getTiltModel() {
        return TheatricalModels.MOVING_HEAD_TILT;
    }

    @Override
    public IBakedModel getPanModel() {
        return TheatricalModels.MOVING_HEAD_PAN;
    }

    @Override
    public float[] getTiltRotationPosition() {
        return new float[]{0.5F, -.6F, -.5F};
    }

    @Override
    public float[] getPanRotationPosition() {
        return new float[]{0.5F, -.5F, -.5F};
    }

    @Override
    public float getDefaultRotation() {
        return 90F;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0F, -0.8F, -0.35F};
    }

    @Override
    public float getBeamWidth() {
        return 0.15F;
    }

    @Override
    public void setTilt(int tilt) {
        super.setTilt(MathHelper.clamp(tilt, -90, 90));
    }

    @Override
    public int getRed() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(4);
    }

    @Override
    public int getGreen() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(5);
    }

    @Override
    public int getBlue() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(6);
    }

    @Override
    public int getPan() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(2);
    }

    @Override
    public int getTilt() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(3);
    }

    @Override
    public int getFocus() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(1);
    }

    @Override
    public float getIntensity() {
        return getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(0);
    }
}
