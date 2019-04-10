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

package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.blocks.base.BlockIlluminator;
import com.georlegacy.general.theatrical.blocks.cables.BlockCable;
import com.georlegacy.general.theatrical.blocks.dimmable.BlockDimmerRack;
import com.georlegacy.general.theatrical.blocks.dimmable.BlockPlugPanel;
import com.georlegacy.general.theatrical.blocks.interfaces.BlockArtnetInterface;
import com.georlegacy.general.theatrical.blocks.interfaces.BlockDMXInterface;
import com.georlegacy.general.theatrical.blocks.rigging.BlockLadderBar;
import com.georlegacy.general.theatrical.blocks.rigging.BlockSquareTruss;
import com.georlegacy.general.theatrical.blocks.rigging.bars.BlockBar;
import com.georlegacy.general.theatrical.blocks.rigging.bars.BlockDMXBar;
import com.georlegacy.general.theatrical.blocks.rigging.bars.BlockDMXPowerBar;
import com.georlegacy.general.theatrical.blocks.rigging.bars.BlockSocapexBar;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;

public class TheatricalBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();


    public static BlockIlluminator BLOCK_ILLUMINATOR = new BlockIlluminator();


    public static BlockBar BLOCK_BAR = new BlockBar();
    public static BlockSocapexBar BLOCK_SOCAPEX_BAR = new BlockSocapexBar();
    public static BlockDMXBar BLOCK_DMX_BAR = new BlockDMXBar();
    public static BlockDMXPowerBar BLOCK_DMX_POWER_BAR = new BlockDMXPowerBar();


    public static BlockLadderBar BLOCK_LADDER_BAR = new BlockLadderBar();
    public static BlockSquareTruss BLOCK_SQUARE_TRUSS = new BlockSquareTruss();


    public static BlockCable BLOCK_CABLE = new BlockCable();
    public static BlockDMXInterface BLOCK_DMX_INTERFACE = new BlockDMXInterface();
    public static BlockArtnetInterface BLOCK_ARTNET_INTERFACE = new BlockArtnetInterface();

    public static BlockDimmerRack BLOCK_DIMMER_RACK = new BlockDimmerRack();
    public static BlockPlugPanel BLOCK_PLUG_PANEL = new BlockPlugPanel();
}
