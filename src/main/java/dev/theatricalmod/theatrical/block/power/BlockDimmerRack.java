package dev.theatricalmod.theatrical.block.power;

import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockDimmerRack extends HorizontalBlock implements ITOPInfoProvider {

    public BlockDimmerRack() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityDimmerRack();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity ent, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                NetworkHooks.openGui((ServerPlayerEntity) ent, (INamedContainerProvider) tileEntity, tileEntity.getPos());
            } else {

            }
            return ActionResultType.PASS;
        }
        return super.onBlockActivated(state, world, pos, ent, hand, blockRayTraceResult);
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityDimmerRack) {
            TileEntityDimmerRack pipe = (TileEntityDimmerRack) tileEntity;
            pipe.getCapability(DMXReceiver.CAP, null).ifPresent(iTheatricalPowerStorage -> {
                for(int i = 0; i < iTheatricalPowerStorage.getChannelCount(); i++){
                    iProbeInfo.text(new StringTextComponent("DMX #" + i + ": " + iTheatricalPowerStorage.getChannel(i)));
                }
            });
        }
    }
}
