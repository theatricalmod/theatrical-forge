package dev.theatricalmod.theatrical.block.interfaces;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXRedstoneInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlockDMXRedstoneInterface extends Block implements EntityBlock {

    public BlockDMXRedstoneInterface() {
        super(TheatricalBlocks.BASE_PROPERTIES.noOcclusion());
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
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        TileEntityDMXRedstoneInterface tile = (TileEntityDMXRedstoneInterface) blockAccess.getBlockEntity(pos);
        if(tile != null) {
            return tile.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityDMXRedstoneInterface(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        return type == TheatricalTiles.DMX_REDSTONE_INTERFACE.get() ? TileEntityDMXRedstoneInterface::tick : null;
    }
}
