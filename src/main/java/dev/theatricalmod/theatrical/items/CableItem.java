package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.block.cables.CableBlock;
import dev.theatricalmod.theatrical.block.cables.CableBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableItem extends Item {

    private CableType type;


    public CableItem(Properties properties, CableType type) {
        super(properties);
        this.type = type;
    }


    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        //TODO: make this work?
        // && !world.getBlockState(pos).getBlock().isReplaceable(world.getBlockState(pos), c)
        if (!(world.getBlockState(pos).getBlock() instanceof CableBlock))
        {
            pos = pos.offset(facing);
        }

        ItemStack stack = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);
//TODO: Find replacment method
//        || world.mayPlace(world.getBlockState(pos).getBlock(), pos, false, facing, null)
        if (player.canPlayerEdit(pos, facing, stack))
        {
            if (!(tileEntity instanceof CableBlockEntity))
            {
                world.setBlockState(pos, TheatricalBlocks.CABLE.getDefaultState(), 11);
                tileEntity = world.getTileEntity(pos);
            }

            if (tileEntity instanceof CableBlockEntity)
            {
                CableBlockEntity tile = (CableBlockEntity) tileEntity;
                tileEntity.markDirty();
                if (type == CableType.DMX) {
                    world.getCapability(WorldDMXNetwork.CAP).ifPresent(t -> t.setRefresh(true));
                } else if (type == CableType.SOCAPEX) {
                    world.getCapability(WorldSocapexNetwork.CAP).ifPresent(t -> t.setRefresh(true));
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
