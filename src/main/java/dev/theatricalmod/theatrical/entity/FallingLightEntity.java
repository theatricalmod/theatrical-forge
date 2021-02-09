package dev.theatricalmod.theatrical.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FallingLightEntity extends FallingBlockEntity {

    public FallingLightEntity(World world) {
        super(EntityType.FALLING_BLOCK, world);
    }

    public FallingLightEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState) {
        super(worldIn, x, y, z, fallingBlockState);
        this.shouldDropItem = false;
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if(entityIn instanceof LivingEntity) {
            entityIn.attackEntityFrom(DamageSource.ANVIL, 10);
        }
    }

    // Don't see any other way to catch when the has just landed
    @Override
    public void remove() {
        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 1, 1);
        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);
        super.remove();
    }
}
