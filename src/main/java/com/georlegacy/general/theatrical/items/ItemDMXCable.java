package com.georlegacy.general.theatrical.items;

import com.georlegacy.general.theatrical.api.capabilities.WorldDMXNetwork;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.items.base.ItemBase;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.cables.TileDMXCable;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDMXCable extends ItemBase {

    public ItemDMXCable() {
        super("dmx_cable");

        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }


    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack stack = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (player.canPlayerEdit(pos, facing, stack) && (tileEntity instanceof TileDMXCable || world.mayPlace(world.getBlockState(pos).getBlock(), pos, false, facing, null)))
        {
            if (!(tileEntity instanceof TileDMXCable))
            {
                world.setBlockState(pos, TheatricalBlocks.BLOCK_CABLE.getDefaultState(), 11);
                tileEntity = world.getTileEntity(pos);
            }

            if (tileEntity instanceof TileDMXCable)
            {
                TileDMXCable tile = (TileDMXCable) tileEntity;
                EnumFacing opposite = facing.getOpposite();

                if (tile.sides[opposite.getIndex()])
                {
                    return EnumActionResult.FAIL;
                }

                tile.sides[opposite.getIndex()] = true;
                tileEntity.markDirty();
                WorldDMXNetwork.getCapability(world).setRefresh(true);
            }

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 11);

            if (player instanceof EntityPlayerMP)
            {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
            }

            SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    }
}
