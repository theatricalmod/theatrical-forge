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

package com.georlegacy.general.theatrical.handlers;

import com.georlegacy.general.theatrical.packets.ChangeDimmerPatchPacket;
import com.georlegacy.general.theatrical.packets.SendDMXPacket;
import com.georlegacy.general.theatrical.packets.UpdateArtNetInterfacePacket;
import com.georlegacy.general.theatrical.packets.UpdateDMXStartAddressPacket;
import com.georlegacy.general.theatrical.packets.UpdateIlluminatorPacket;
import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class TheatricalPacketHandler {

    public static SimpleNetworkWrapper INSTANCE;


    public static void init() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(
            Reference.MOD_ID);
        INSTANCE
            .registerMessage(UpdateLightPacket.ServerHandler.class, UpdateLightPacket.class, 0,
                Side.SERVER);
        INSTANCE
            .registerMessage(UpdateIlluminatorPacket.ServerHandler.class,
                UpdateIlluminatorPacket.class, 1,
                Side.SERVER);
        INSTANCE
            .registerMessage(UpdateDMXStartAddressPacket.ServerHandler.class,
                UpdateDMXStartAddressPacket.class, 2,
                Side.SERVER);
        INSTANCE
            .registerMessage(UpdateArtNetInterfacePacket.ServerHandler.class,
                UpdateArtNetInterfacePacket.class, 8,
                Side.SERVER);
        INSTANCE
            .registerMessage(ChangeDimmerPatchPacket.ServerHandler.class,
                ChangeDimmerPatchPacket.class, 9,
                Side.SERVER);
    }

    public static void clientInit(){
        INSTANCE
            .registerMessage(UpdateLightPacket.ClientHandler.class, UpdateLightPacket.class, 3,
                Side.CLIENT);
        INSTANCE
            .registerMessage(UpdateIlluminatorPacket.ClientHandler.class,
                UpdateIlluminatorPacket.class, 4,
                Side.CLIENT);
        INSTANCE
            .registerMessage(SendDMXPacket.ClientHandler.class, SendDMXPacket.class, 5,
                Side.CLIENT);
        INSTANCE
            .registerMessage(UpdateDMXStartAddressPacket.ClientHandler.class,
                UpdateDMXStartAddressPacket.class, 6,
                Side.CLIENT);
        INSTANCE
            .registerMessage(UpdateArtNetInterfacePacket.ClientHandler.class,
                UpdateArtNetInterfacePacket.class, 7,
                Side.CLIENT);
        INSTANCE
            .registerMessage(ChangeDimmerPatchPacket.ClientHandler.class,
                ChangeDimmerPatchPacket.class, 10,
                Side.CLIENT);
    }

}
