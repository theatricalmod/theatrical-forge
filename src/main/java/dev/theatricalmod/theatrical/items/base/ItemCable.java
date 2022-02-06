package dev.theatricalmod.theatrical.items.base;

import dev.theatricalmod.theatrical.api.CableSide;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import java.util.List;
import javax.annotation.Nullable;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.block.cables.BlockCable;
import dev.theatricalmod.theatrical.tiles.TileEntityCable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

public class ItemCable extends ItemBase {

    private CableType type;

    public ItemCable(Item.Properties properties, CableType type) {
        super(properties);
        this.type = type;
    }

    private boolean canPlace(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        return blockState.isValidPosition(world, pos) && world.placedBlockCollides(blockState, pos, ISelectionContext.forEntity(player));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();

        if (!(world.getBlockState(pos).getBlock() instanceof BlockCable) && !world.getBlockState(pos).getBlock().isReplaceable(world.getBlockState(pos), new BlockItemUseContext(context)))
        {
            pos = pos.offset(facing);
        }

        ItemStack stack = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (player.canPlayerEdit(pos, facing, stack) && (tileEntity instanceof TileEntityCable || canPlace(TheatricalBlocks.CABLE.get().getDefaultState(), world, pos, player)))
        {
            if (!(tileEntity instanceof TileEntityCable))
            {
                world.setBlockState(pos, TheatricalBlocks.CABLE.get().getDefaultState(), 11);
                tileEntity = world.getTileEntity(pos);
            }

            if (tileEntity instanceof TileEntityCable)
            {
                TileEntityCable tile = (TileEntityCable) tileEntity;
                Direction opposite = facing.getOpposite();

                if (tile.hasSide(opposite.getIndex()))
                {
                    if(tile.getSide(opposite.getIndex()).hasType(type)){
                        if (!player.isSneaking()) {
                            return ActionResultType.FAIL;
                        } else {
                            CableSide side = tile.getSide(opposite.getIndex());
                            side.removeType(type);
                            tile.setSide(opposite.getIndex(), side);
                            if (side.getTotalTypes() <= 0) {
                                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                            }
                        }
                    }else{
                        CableSide side = tile.getSide(opposite.getIndex());
                        side.addType(type);
                        tile.setSide(opposite.getIndex(), side);
                    }
                }else{
                    boolean haveAdded = false;
                    CableSide cableSide = tile.newSide();
                    for (Direction facing1 : Direction.Plane.HORIZONTAL) {
                        BlockPos offset = pos.offset(facing1);
                        if (world.getBlockState(offset).getBlock() instanceof BlockCable) {
                            TileEntityCable cable = (TileEntityCable) world.getTileEntity(offset);
                            if (cable != null && cable.hasSide(opposite.getIndex())) {
                                if (cable.getSide(opposite.getIndex()).hasType(type)) {
                                    int slot = cable.getSide(opposite.getIndex()).getSlotForType(type);
                                    cableSide.setSlotToType(type, slot);
                                    haveAdded = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!haveAdded) {
                        cableSide.addType(type);
                    }
                    tile.setSide(opposite.getIndex(), cableSide);
                }
                tileEntity.markDirty();
                tile.requestModelDataUpdate();
                if (type == CableType.DMX) {
                    world.getCapability(WorldDMXNetwork.CAP).ifPresent(cap -> cap.setRefresh(true));
                } else if (type == CableType.SOCAPEX) {
                    world.getCapability(WorldSocapexNetwork.CAP).ifPresent(cap -> cap.setRefresh(true));
                }
            }

            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 11);

            if (player instanceof ServerPlayerEntity)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
            }

            SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            stack.shrink(1);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }


    public CableType getType() {
        return type;
    }
}
