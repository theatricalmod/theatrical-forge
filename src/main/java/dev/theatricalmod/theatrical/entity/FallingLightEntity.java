package dev.theatricalmod.theatrical.entity;

import dev.theatricalmod.theatrical.block.BlockHangable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FallingLightEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {


    public FallingLightEntity(EntityType<? extends FallingLightEntity> type, Level world) {
        super(type, world);
    }

    public FallingLightEntity(Level worldIn, double x, double y, double z, BlockState fallingBlockState) {
        this(TheatricalEntities.FALLING_LIGHT.get(), worldIn);
        this.blockState = fallingBlockState;
        this.setPos(x, y + (double)((1.0F - this.getBbHeight()) / 2.0F), z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float p_149644_, DamageSource p_149645_) {
        int i = Mth.ceil(fallDistance - 1.0F);
        if (i > 0) {
            List<Entity> list = this.level.getEntities(this, this.getBoundingBox());
            for (Entity entity : list) {
                AtomicInteger damage = new AtomicInteger(4);
                AtomicInteger maxDamage = new AtomicInteger(60);
                entity.getArmorSlots().forEach(stack -> {
                    if(EquipmentSlot.HEAD.equals(stack.getEquipmentSlot())) {
                        damage.updateAndGet(v -> v / 2);
                        maxDamage.updateAndGet(v -> v / 2);
                    }
                });
                entity.hurt(DamageSource.FALLING_BLOCK, (float) Math.min(Mth.floor((float) i * damage.get()), maxDamage.get()));
            }
        }
        return false;
    }

    @Override
    public void tick() {
        Block block = this.blockState.getBlock();
        BlockPos currentPos;
        //Called every tick to increment the fall time, and also calls the if statement method on first tick
        if (this.time++ == 0) {
            currentPos = this.blockPosition();
            if (this.level.getBlockState(currentPos).is(block)) {
                this.level.removeBlock(currentPos, false);
            } else if (!this.level.isClientSide) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
        }
        //Do motion
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        //If on ground, handle turning back into a block
        if (!this.level.isClientSide) {
            currentPos = this.blockPosition();
            if (this.isOnGround()) {
                BlockState blockstate = this.level.getBlockState(currentPos);
                this.remove(RemovalReason.DISCARDED);
                if (blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, currentPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
                    if (this.level.setBlock(currentPos, this.blockState, 3)) {
                        this.getCommandSenderWorld().levelEvent(2008, new BlockPos(this.getX(), this.getY(), this.getZ()), 0);
                        this.getCommandSenderWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 1, 1);
                        this.getCommandSenderWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1, 1);
                        this.level.setBlockAndUpdate(currentPos, this.blockState.setValue(BlockHangable.BROKEN, true));
                        if (this.blockData != null && this.blockState.hasBlockEntity()) {
                            BlockEntity tileentity = this.level.getBlockEntity(currentPos);
                            if (tileentity != null) {
                                CompoundTag compoundnbt = tileentity.saveWithFullMetadata();
                                for(String s : this.blockData.getAllKeys()) {
                                    Tag inbt = this.blockData.get(s);
                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                        compoundnbt.put(s, inbt.copy());
                                    }
                                }
                                tileentity.load(compoundnbt);
                                tileentity.setChanged();
                            }
                        }
                    }
                }
            }
            else if (!this.level.isClientSide && (this.time > 100 && (currentPos.getY() < 1 || currentPos.getY() > 256) || this.time > 600)) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        //Copied from vanilla
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
    }

    @Override
    @Nonnull
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(Block.getId(this.getBlockState()));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.blockState = Block.stateById(additionalData.readInt());
    }
}
