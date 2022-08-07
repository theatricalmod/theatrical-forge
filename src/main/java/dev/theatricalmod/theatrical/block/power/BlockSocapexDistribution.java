package dev.theatricalmod.theatrical.block.power;

import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.power.TileEntitySocapexDistribution;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class BlockSocapexDistribution extends DirectionalBlock implements ITOPInfoProvider, EntityBlock {

    public BlockSocapexDistribution() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntitySocapexDistribution) {
            TileEntitySocapexDistribution pipe = (TileEntitySocapexDistribution) tileEntity;
            pipe.getCapability(SocapexReceiver.CAP, pipe.getFacing()).ifPresent(iTheatricalPowerStorage -> {
                for(Direction direction : Direction.values()) {
                    if(direction == pipe.getFacing()){
                        continue;
                    }
                    int index = pipe.getDirectionalIndex(direction);
                    iProbeInfo.text(new TextComponent("Socapex (" + (index + 1) + ") " + (direction.getSerializedName()) + ": " + iTheatricalPowerStorage.getEnergyStored(index)));
                }
            });
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntitySocapexDistribution(blockPos, blockState);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == TheatricalTiles.SOCAPEX_DISTRIBUTION.get() ? TileEntitySocapexDistribution::tick : null;
    }
}
