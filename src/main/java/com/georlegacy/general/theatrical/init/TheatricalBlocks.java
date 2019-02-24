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
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;

import com.georlegacy.general.theatrical.blocks.rigging.BlockBar;
import com.georlegacy.general.theatrical.blocks.rigging.BlockLadderBar;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

public class TheatricalBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();


    public static BlockIlluminator BLOCK_ILLUMINATOR = new BlockIlluminator();

    public static BlockFresnel BLOCK_FRESNEL = new BlockFresnel();

    public static BlockBar BLOCK_BAR = new BlockBar();
    public static BlockLadderBar BLOCK_LADDER_BAR = new BlockLadderBar();

}
