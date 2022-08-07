package dev.theatricalmod.theatrical.block.power;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockDimmerRack extends HorizontalDirectionalBlock implements ITOPInfoProvider, EntityBlock {

    public BlockDimmerRack() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        return type == TheatricalTiles.DIMMER_RACK.get() ? TileEntityDimmerRack::tick : null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }



    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof MenuProvider) {
            MenuProvider provider = (MenuProvider) tileEntity;
            AbstractContainerMenu container = provider.createMenu(0, player.getInventory(), player);
            if (container != null) {
                if (player instanceof ServerPlayer) {
                    NetworkHooks.openGui((ServerPlayer) player, provider, buffer -> {
                        buffer.writeBlockPos(pos);
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityDimmerRack) {
            TileEntityDimmerRack pipe = (TileEntityDimmerRack) tileEntity;
            pipe.getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, null).ifPresent(iTheatricalPowerStorage -> {
                for(int i = 0; i < iTheatricalPowerStorage.getChannelCount(); i++){
                    iProbeInfo.text(new TextComponent("DMX #" + i + ": " + iTheatricalPowerStorage.getChannel(i)));
                }
            });
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityDimmerRack(pos, state);
    }
}
