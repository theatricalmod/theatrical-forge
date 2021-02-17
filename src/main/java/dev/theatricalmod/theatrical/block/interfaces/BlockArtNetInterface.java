package dev.theatricalmod.theatrical.block.interfaces;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockArtNetInterface extends Block {

    public BlockArtNetInterface() {
        super(TheatricalBlocks.BASE_PROPERTIES.notSolid());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityArtNetInterface();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileEntityArtNetInterface && placer != null){
            ((TileEntityArtNetInterface)worldIn.getTileEntity(pos)).setPlayer(placer.getUniqueID());
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) tileEntity;
            Container container = provider.createMenu(0, player.inventory, player);
            if (container != null) {
                if (player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, provider, buffer -> {
                        buffer.writeBlockPos(pos);
                    });
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(p_225533_1_, world, pos, player, p_225533_5_, p_225533_6_);
    }

}
