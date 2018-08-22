package com.georlegacy.general.theatrical.proxy;

import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import net.minecraft.client.Minecraft;

/**
 * Client Proxy for Theatrical
 *
 * @author James Conway
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void registerModelBakeryVariants() {
    }

    @Override
    public void registerColorBlocks() {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(TheatricalBlocks.blockFresnel, TheatricalBlocks.blockFresnel);
    }

}
