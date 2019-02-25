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
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IBarAttachable;
import com.georlegacy.general.theatrical.init.TheatricalModels;
import com.georlegacy.general.theatrical.tile.TileTungstenFixture;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;

@Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")
public class TileFresnel extends TileTungstenFixture implements IPeripheral {
    @Nonnull
    @Override
    public String getType() {
        return "fresnel";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{"setPan", "setTilt", "setFocus", "getPan", "getTilt", "getFocus"};
    }

    @Method(modid = "computercraft")
    @Nullable
    @Override
    public Object[] callMethod(@Nonnull IComputerAccess cpu,
        @Nonnull ILuaContext ctx, int method, @Nonnull Object[] args)
        throws LuaException, InterruptedException {
        switch (method) {
            case 0:
                this.setPan(((Number) args[0]).intValue());
                break;
            case 1:
                this.setTilt(((Number) args[0]).intValue());
                break;
            case 2:
                this.setFocus(((Number) args[0]).intValue());
                break;
            case 3:
                return new Object[]{this.getPan()};
            case 4:
                return new Object[]{this.getTilt()};
            case 5:
                return new Object[]{this.getFocus()};
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral != null && iPeripheral.getType().equals(this.getType());
    }


    @Override
    public Class<? extends Block> getBlock() {
        return BlockFresnel.class;
    }

    @Override
    public float getMaxLightDistance() {
        return 7;
    }

    @Override
    public HangableType getHangType() {
        return HangableType.HOOK_BAR;
    }

    @Override
    public IBakedModel getStaticModel() {
        Block block = getWorld().getBlockState(pos).getBlock();
        if(block instanceof IBarAttachable && ((IBarAttachable) block).isOnBar(world, pos)){
            return TheatricalModels.FRESNEL_HOOK_BAR;
        }
        return TheatricalModels.FRESNEL_HOOK;
    }

    @Override
    public IBakedModel getTiltModel() {
        return TheatricalModels.FRESNEL_BODY;
    }

    @Override
    public IBakedModel getPanModel() {
        return TheatricalModels.FRESNEL_HANDLE;
    }

    @Override
    public float[] getTiltRotationPosition() {
        return new float[]{0.7F, -.75F, -.64F};
    }

    @Override
    public float[] getPanRotationPosition() {
        return new float[]{0.5F, 0, -.6F};
    }

    @Override
    public float getDefaultRotation() {
        return 0;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0F, -1.5F, -1F};
    }

    @Override
    public float getBeamWidth() {
        return 0.25F;
    }
}
