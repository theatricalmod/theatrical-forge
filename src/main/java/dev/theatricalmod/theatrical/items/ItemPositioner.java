package dev.theatricalmod.theatrical.items;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemPositioner extends Item {


    public ItemPositioner(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(!worldIn.isClientSide) {
            if (playerIn.isShiftKeyDown()) {
                CompoundTag lightTag = playerIn.getItemInHand(handIn).getTagElement("light");
                if (lightTag != null) {
                    BlockPos lightPos = NbtUtils.readBlockPos(lightTag);
                    BlockEntity tile = worldIn.getBlockEntity(lightPos);
                    if (tile instanceof TileEntityGenericFixture) {
                        TileEntityGenericFixture tileEntityGenericFixture = (TileEntityGenericFixture) tile;
                        tileEntityGenericFixture.setTrackingEntity(null);
                        playerIn.getItemInHand(handIn).removeTagKey("light");
                    }
                }
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
