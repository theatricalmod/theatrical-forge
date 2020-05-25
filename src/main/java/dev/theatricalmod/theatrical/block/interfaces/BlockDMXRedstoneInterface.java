package dev.theatricalmod.theatrical.block.interfaces;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXRedstoneInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockDMXRedstoneInterface extends Block {

    public BlockDMXRedstoneInterface() {
        super(TheatricalBlocks.BASE_PROPERTIES.notSolid());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityDMXRedstoneInterface();
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
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        TileEntityDMXRedstoneInterface tile = (TileEntityDMXRedstoneInterface) blockAccess.getTileEntity(pos);
        if(tile != null) {
            return tile.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }
}
