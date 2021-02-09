package dev.theatricalmod.theatrical.entity;

import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FallingLightEntity extends FallingBlockEntity {

    public FallingLightEntity(EntityType<? extends FallingLightEntity> type, World world) {
        super(type, world);
    }

    public FallingLightEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState) {
        this(TheatricalEntities.FALLING_LIGHT.get(), worldIn);
        this.fallTile = fallingBlockState;
        this.preventEntitySpawning = true;
        this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
        this.setMotion(Vector3d.ZERO);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(this.getPosition());
    }

    @Override
    public void tick() {
        Block block = this.fallTile.getBlock();
        BlockPos currentPos;
        //Called every tick to increment the fall time, and also calls the if statement method on first tick
        if (this.fallTime++ == 0) {
            currentPos = this.getPosition();
            if (this.world.getBlockState(currentPos).isIn(block)) {
                this.world.removeBlock(currentPos, false);
            } else if (!this.world.isRemote) {
                this.remove();
                return;
            }
        }
        //Do motion
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getMotion());
        //If on ground, handle turning back into a block
        if (!this.world.isRemote) {
            currentPos = this.getPosition();
            if (this.isOnGround()) {
                LOGGER.warn("2");
                BlockState blockstate = this.world.getBlockState(currentPos);
                this.remove();
                if (blockstate.isReplaceable(new DirectionalPlaceContext(this.world, currentPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
                    if (this.world.setBlockState(currentPos, this.fallTile, 3)) {
                        this.getEntityWorld().playEvent(2008, new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ()), 0);
                        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 1, 1);
                        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);

                        if (this.tileEntityData != null && this.fallTile.hasTileEntity()) {
                            TileEntity tileentity = this.world.getTileEntity(currentPos);
                            if (tileentity != null) {
                                CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                                for(String s : this.tileEntityData.keySet()) {
                                    INBT inbt = this.tileEntityData.get(s);
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                        compoundnbt.put(s, inbt.copy());
                                    }
                                }
                                tileentity.read(this.fallTile, compoundnbt);
                                tileentity.markDirty();
                            }
                        }
                    }
                }
            }
        }
        //Copied from vanilla
        this.setMotion(this.getMotion().scale(0.98D));
    }
}
