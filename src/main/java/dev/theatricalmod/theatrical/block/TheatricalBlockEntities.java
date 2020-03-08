package dev.theatricalmod.theatrical.block;


import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.cables.CableBlockEntity;
import dev.theatricalmod.theatrical.block.interfaces.ArtNetInterfaceBlockEntity;
import dev.theatricalmod.theatrical.block.light.IlluminatorBlockEntity;
import dev.theatricalmod.theatrical.block.light.IntelligentFixtureBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TheatricalMod.MOD_ID)
public class TheatricalBlockEntities {

    public static final TileEntityType<IntelligentFixtureBlockEntity> MOVING_LIGHT = null;
    public static final TileEntityType<CableBlockEntity> CABLE = null;
    public static final TileEntityType<IlluminatorBlockEntity> ILLUMINATOR = null;
    public static final TileEntityType<ArtNetInterfaceBlockEntity> ARTNET_INTERFACE = null;

}
