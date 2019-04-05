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

import com.georlegacy.general.theatrical.TheatricalConfig;
import com.georlegacy.general.theatrical.api.HangableType;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockMovingHead;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.tiles.TileRGBFixture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class TileMovingHead extends TileRGBFixture {

    public TileMovingHead() {
        super(18, 0, 5, TheatricalConfig.general.movingHeadEnergyCost, TheatricalConfig.general.movingHeadEnergyUsage);
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
        Block block = getWorld().getBlockState(pos).getBlock();
        if(block instanceof BlockHangable && ((BlockHangable) block).isHanging(world, pos)){
            return TheatricalModels.MOVING_HEAD_BAR;
        }
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
        return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(2));
    }

    @Override
    public int getGreen() {
        return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(3));
    }

    @Override
    public int getBlue() {
        return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(4));
    }

    @Override
    public int getPan() {
        if (this.power < this.energyCost) {
            return prevPan;
        }
        return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(6))  * 360) / 255F);
    }

    @Override
    public int getTilt() {
        if (this.power < this.energyCost) {
            return prevTilt;
        }
        return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(8)) * 180) / 255F) - 90;
    }

    @Override
    public int getFocus() {
        return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(12));
    }

    @Override
    public float getIntensity() {
        if (this.power < this.energyCost) {
            return 0;
        }
        return convertByte(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(1));
    }

    public float convertByte(byte val){
        return val & 0xFF;
    }

    public int convertByteToInt(byte val){
        return val & 0xFF;
    }

    @Override
    public void update() {
        super.update();
        prevTilt = getTilt();
        prevPan = getPan();
    }

    @Override
    public int getColorHex() {
        return (getRed() << 16) | (getGreen() << 8) | getBlue();
    }

}
