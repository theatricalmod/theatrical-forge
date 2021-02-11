package dev.theatricalmod.theatrical.data;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.items.TheatricalItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class TheatricalUSLangProvider extends LanguageProvider {
    public TheatricalUSLangProvider(DataGenerator gen) {
        super(gen, TheatricalMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.theatrical", "Theatrical");
        this.addBlock(TheatricalBlocks.MOVING_LIGHT, "Moving Light");
        this.addBlock(TheatricalBlocks.IWB, "Internally Wired Bar (IWB)");
        this.addBlock(TheatricalBlocks.TRUSS, "MT100 Truss");
        this.addBlock(TheatricalBlocks.TEST_DMX, "Test DMX Block");
        this.addBlock(TheatricalBlocks.ARTNET_INTERFACE, "ArtNet Interface");
        this.addBlock(TheatricalBlocks.DMX_CABLE, "DMX Cable");
        this.addBlock(TheatricalBlocks.DIMMER_RACK, "Dimmer Rack");
        this.addBlock(TheatricalBlocks.SOCAPEX_DISTRIBUTION, "Socapex Distro");
        this.addBlock(TheatricalBlocks.GENERIC_LIGHT, "Generic Light");
        this.addBlock(TheatricalBlocks.POWER_CABLE, "Power Cable");
        this.addBlock(TheatricalBlocks.DIMMED_POWER_CABLE, "Dimmed Power Cable");
        this.addBlock(TheatricalBlocks.BASIC_LIGHTING_DESK, "Basic Lighting Desk");
        this.addBlock(TheatricalBlocks.DMX_REDSTONE_INTERFACE, "DMX-Redstone Interface");
        this.addItem(TheatricalItems.POSITIONER, "Remote Light Positioner");
        this.addItem(TheatricalItems.COG, "Mechanical Cog");
        this.addItem(TheatricalItems.LED, "LED");
        this.addItem(TheatricalItems.MOTOR, "Electrical Motor");
        this.addItem(TheatricalItems.BULB, "Tungsten Bulb");
        this.addItem(TheatricalItems.WRENCH, "Wrench");
    }
}
