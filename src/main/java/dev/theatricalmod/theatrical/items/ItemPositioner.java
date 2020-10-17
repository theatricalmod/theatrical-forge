package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPositioner extends Item {


    public ItemPositioner(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                CompoundNBT lightTag = playerIn.getHeldItem(handIn).getChildTag("light");
                if (lightTag != null) {
                    BlockPos lightPos = NBTUtil.readBlockPos(lightTag);
                    TileEntity tile = worldIn.getTileEntity(lightPos);
                    if (tile instanceof TileEntityGenericFixture) {
                        TileEntityGenericFixture tileEntityGenericFixture = (TileEntityGenericFixture) tile;
                        tileEntityGenericFixture.setTrackingEntity(null);
                        playerIn.getHeldItem(handIn).removeChildTag("light");
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
