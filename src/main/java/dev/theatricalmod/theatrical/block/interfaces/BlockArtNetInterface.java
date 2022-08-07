package dev.theatricalmod.theatrical.block.interfaces;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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

public class BlockArtNetInterface extends Block implements EntityBlock {

    public BlockArtNetInterface() {
        super(TheatricalBlocks.BASE_PROPERTIES.noOcclusion());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(worldIn.getBlockEntity(pos) instanceof TileEntityArtNetInterface && placer != null){
            ((TileEntityArtNetInterface)worldIn.getBlockEntity(pos)).setPlayer(placer.getUUID());
        }
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public InteractionResult use(BlockState p_225533_1_, Level world, BlockPos pos, Player player, InteractionHand p_225533_5_, BlockHitResult p_225533_6_) {
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
        return super.use(p_225533_1_, world, pos, player, p_225533_5_, p_225533_6_);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityArtNetInterface(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        return type == TheatricalTiles.ARTNET_INTERFACE.get() ? TileEntityArtNetInterface::tick : null;
    }
}
