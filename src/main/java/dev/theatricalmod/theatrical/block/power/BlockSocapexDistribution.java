package dev.theatricalmod.theatrical.block.power;

import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.power.TileEntitySocapexDistribution;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSocapexDistribution extends DirectionalBlock implements ITOPInfoProvider {

    public BlockSocapexDistribution() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState().with(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntitySocapexDistribution();
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntitySocapexDistribution) {
            TileEntitySocapexDistribution pipe = (TileEntitySocapexDistribution) tileEntity;
            pipe.getCapability(SocapexReceiver.CAP, pipe.getFacing()).ifPresent(iTheatricalPowerStorage -> {
                for(int i = 0; i < 5; i++){
                    iProbeInfo.text(new StringTextComponent("Socapex #" + i + ": " + iTheatricalPowerStorage.getEnergyStored(i)));
                }
            });
        }
    }
}
