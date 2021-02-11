package dev.theatricalmod.theatrical.items;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends Item {

    public ItemWrench(Properties properties) {
        super(properties);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(context.getPlayer() != null) {
            BlockPos blockpos = context.getPos();
            World world = context.getWorld();
            Rotation rot = context.getPlayer().isSneaking() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
            BlockState rotated = world.getBlockState(blockpos).rotate(world, blockpos, rot);
            if (!rotated.equals(world.getBlockState(blockpos))) {
                world.setBlockState(blockpos, rotated);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}