package dev.theatricalmod.theatrical.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class ItemWrench extends Item {

    public ItemWrench(Properties properties) {
        super(properties);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getPlayer() != null) {
            BlockPos blockpos = context.getClickedPos();
            Level world = context.getLevel();
            Rotation rot = context.getPlayer().isShiftKeyDown() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
            BlockState rotated = world.getBlockState(blockpos).rotate(world, blockpos, rot);
            if (!rotated.equals(world.getBlockState(blockpos))) {
                world.setBlockAndUpdate(blockpos, rotated);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}