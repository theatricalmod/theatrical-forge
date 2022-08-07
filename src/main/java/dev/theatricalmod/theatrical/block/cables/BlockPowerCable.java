package dev.theatricalmod.theatrical.block.cables;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmedPowerCable;
import dev.theatricalmod.theatrical.tiles.power.TileEntityPowerCable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockPowerCable extends BlockCable implements ITOPInfoProvider {

    public BlockPowerCable() {
        super(CableType.DIMMED_POWER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityPowerCable(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        return type == TheatricalTiles.POWER_CABLE.get() ? TileEntityPowerCable::tick : null;
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityDimmedPowerCable) {
            TileEntityDimmedPowerCable pipe = (TileEntityDimmedPowerCable) tileEntity;
            pipe.getCapability(TheatricalPower.CAP, null).ifPresent(iTheatricalPowerStorage -> {
                iProbeInfo.text(new TextComponent("Power: " + iTheatricalPowerStorage.getEnergyStored()));
            });
        }
    }
}
