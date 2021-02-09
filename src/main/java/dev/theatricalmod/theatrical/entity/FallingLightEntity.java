package dev.theatricalmod.theatrical.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
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
//        if (this.fallTime++ == 0) {
//            BlockPos currentPosition = this.getPosition();
//            if (this.world.getBlockState(currentPosition).isIn(this.fallTile.getBlock())) {
//                this.world.removeBlock(currentPosition, false);
//            }
//            else if (!this.world.isRemote) {
//                this.remove();
//                return;
//            }
//        }
//        this.move(MoverType.SELF, this.getMotion());
//        if (!this.world.isRemote) {
//            BlockPos currentPosition = this.getPosition();
//            if (this.isOnGround()) {
//                LOGGER.warn("2");
//                BlockState blockstate = this.world.getBlockState(currentPosition);
//                this.remove();
//                if (blockstate.isReplaceable(new DirectionalPlaceContext(this.world, currentPosition, Direction.DOWN, ItemStack.EMPTY, Direction.UP))) {
//                    if (this.fallTile.hasProperty(BlockStateProperties.WATERLOGGED) && this.world.getFluidState(currentPosition).getFluid() == Fluids.WATER) {
//                        this.fallTile = this.fallTile.with(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
//                    }
//
//                    if (this.world.setBlockState(currentPosition, this.fallTile, 3)) {
//                        this.getEntityWorld().playEvent(2008, new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ()), 0);
//                        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 1, 1);
//                        this.getEntityWorld().playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1, 1);
//
//                        if (this.tileEntityData != null && this.fallTile.hasTileEntity()) {
//                            TileEntity tileentity = this.world.getTileEntity(currentPosition);
//                            if (tileentity != null) {
//                                CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
//
//                                for(String s : this.tileEntityData.keySet()) {
//                                    INBT inbt = this.tileEntityData.get(s);
//                                    if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
//                                        compoundnbt.put(s, inbt.copy());
//                                    }
//                                }
//
//                                tileentity.read(this.fallTile, compoundnbt);
//                                tileentity.markDirty();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        this.setMotion(this.getMotion().scale(0.98D));
    }
}
