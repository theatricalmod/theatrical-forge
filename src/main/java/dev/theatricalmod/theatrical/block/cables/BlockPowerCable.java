package dev.theatricalmod.theatrical.block.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.power.TileEntityPowerCable;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPowerCable extends BlockCable implements ITOPInfoProvider {

    public BlockPowerCable() {
        super(CableType.POWER);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityPowerCable();
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityPowerCable) {
            TileEntityPowerCable pipe = (TileEntityPowerCable) tileEntity;
            pipe.getCapability(TheatricalPower.CAP, null).ifPresent(iTheatricalPowerStorage -> {
                iProbeInfo.text(new StringTextComponent("Power: " + iTheatricalPowerStorage.getEnergyStored()));
            });
        }
    }
}
