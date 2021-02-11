package dev.theatricalmod.theatrical.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FallingLightEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {


    public FallingLightEntity(EntityType<? extends FallingLightEntity> type, World world) {
        super(type, world);
    }

    public FallingLightEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState) {
        this(TheatricalEntities.FALLING_LIGHT.get(), worldIn);
        this.fallTile = fallingBlockState;
        this.setPosition(x, y + (double)((1.0F - this.getHeight()) / 2.0F), z);
        this.setMotion(Vector3d.ZERO);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setOrigin(this.getPosition());
    }

    @Override
    public boolean onLivingFall(float fallDistance, float p_225503_2_) {
        int i = MathHelper.ceil(fallDistance - 1.0F);
        if (i > 0) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
            for (Entity entity : list) {
                AtomicInteger damage = new AtomicInteger(4);
                AtomicInteger maxDamage = new AtomicInteger(60);
                entity.getArmorInventoryList().forEach(stack -> {
                    if(EquipmentSlotType.HEAD.equals(stack.getEquipmentSlot())) {
                        damage.updateAndGet(v -> v / 2);
                        maxDamage.updateAndGet(v -> v / 2);
                    }
                });
                entity.attackEntityFrom(DamageSource.FALLING_BLOCK, (float) Math.min(MathHelper.floor((float) i * damage.get()), maxDamage.get()));
            }
        }
        return false;
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
            else if (!this.world.isRemote && (this.fallTime > 100 && (currentPos.getY() < 1 || currentPos.getY() > 256) || this.fallTime > 600)) {
                this.remove();
            }
        }
        //Copied from vanilla
        this.setMotion(this.getMotion().scale(0.98D));
    }

    @Override
    @Nonnull
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(Block.getStateId(this.getBlockState()));
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.fallTile = Block.getStateById(additionalData.readInt());
    }
}
