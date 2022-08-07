package dev.theatricalmod.theatrical.tiles.lights;

import com.mojang.authlib.GameProfile;
import dev.theatricalmod.theatrical.TheatricalConfigHandler;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.api.fixtures.IFixture;
import dev.theatricalmod.theatrical.api.fixtures.IFixtureModelProvider;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.block.light.BlockIlluminator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class TileEntityFixture extends BlockEntity implements IFixture, IFixtureModelProvider {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityFixture tile = (TileEntityFixture) be;
        tile.prevFocus = tile.focus;
        tile.prevPan = tile.pan;
        tile.prevTilt = tile.tilt;
        if (!level.isClientSide) {
            tile.timer++;
            if (tile.timer >= 5) {
                if (tile.shouldTrace()) {
                    tile.distance = tile.doRayTrace();
                    level.sendBlockUpdated(pos, state, state, 11);
                    if (tile.lightBlock != null && tile.emitsLight()) {
                        float newVal = tile.getIntensity() / 255F;
                        int lightval = (int) (newVal * 15F);
                        if(level.getBlockState(tile.lightBlock).isAir() || !(level.getBlockState(tile.lightBlock).getBlock() instanceof BlockIlluminator)){
                            level.setBlock(tile.lightBlock,
                                    TheatricalBlocks.ILLUMINATOR.get().defaultBlockState().setValue(BlockIlluminator.lightValue, lightval), 3);
                            level.getLightEmission(tile.lightBlock);
                        } else {
                            if(level.getBlockState(tile.lightBlock).getValue(BlockIlluminator.lightValue) != lightval){
                                level.setBlockAndUpdate(tile.lightBlock, level.getBlockState(tile.lightBlock).setValue(BlockIlluminator.lightValue, lightval));
                            }
                        }
                    }
                }
                tile.timer = 0;
            }
        }
    }
    private Fixture fixture;
    private double distance = 0;
    private int pan, tilt = 0;
    private int focus = 6;

    private long timer = 0;

    public int prevTilt, prevPan, prevFocus = 0;

    private BlockPos lightBlock;

    private FakePlayer fakePlayer;

    public TileEntityFixture(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public BlockPos getLightBlock() {
        return lightBlock;
    }

    public void setLightBlock(BlockPos lightBlock) {
        this.lightBlock = lightBlock;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public CompoundTag getNBT(@Nullable CompoundTag compoundNBT) {
        if (compoundNBT == null) {
            compoundNBT = new CompoundTag();
        }
        compoundNBT.putInt("pan", this.pan);
        compoundNBT.putInt("tilt", this.tilt);
        compoundNBT.putInt("focus", this.focus);
        compoundNBT.putInt("prevPan", prevPan);
        compoundNBT.putInt("prevTilt", prevTilt);
        compoundNBT.putInt("prevFocus", prevFocus);
        compoundNBT.putLong("timer", timer);
        compoundNBT.putDouble("distance", distance);
        if (getFixture() != null) {
            compoundNBT.putString("fixture_type", getFixture().getRegistryName().toString());
        }
        return compoundNBT;
    }

    public void readNBT(CompoundTag compoundNBT) {
        pan = compoundNBT.getInt("pan");
        tilt = compoundNBT.getInt("tilt");
        focus = compoundNBT.getInt("focus");
        prevPan = compoundNBT.getInt("prevPan");
        prevTilt = compoundNBT.getInt("prevTilt");
        prevFocus = compoundNBT.getInt("prevFocus");
        timer = compoundNBT.getLong("timer");
        distance = compoundNBT.getDouble("distance");
        if (compoundNBT.contains("fixture_type")) {
            setFixture(Fixture.getRegistry().get().getValue(new ResourceLocation(compoundNBT.getString("fixture_type"))));
        } else {
            setFixture(null);
        }
    }

    public int getExtraTilt(){
        return 0;
    }

    public final Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    @Override
    public boolean emitsLight() {
        return !getBlockState().getValue(BlockHangable.BROKEN) && TheatricalConfigHandler.COMMON.emitLight.get();
    }

    public double doRayTrace() {
        BlockState blockState = level.getBlockState(worldPosition);
        Direction direction = blockState.getValue(
            HorizontalDirectionalBlock.FACING);
        float lookingAngle = direction.toYRot();
        lookingAngle = (isUpsideDown() ? lookingAngle + getPan() : lookingAngle - getPan());
        lookingAngle = lookingAngle % 360;

        float tilt = getTilt() + getExtraTilt();
        if (!isUpsideDown()) {
            tilt = -tilt;
        }

        Vec3 look = getVectorForRotation(tilt, lookingAngle);
        double distance = getMaxLightDistance();
        Vec3 startVec = look.scale(0.9F).add(worldPosition.getX() + 0.5, worldPosition.getY() + 0.51, worldPosition.getZ() + 0.5);
        Vec3 endVec = look.scale(distance).add(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        level.addParticle(DustParticleOptions.REDSTONE, endVec.x, endVec.y, endVec.z, 0, 0, 0);
        if (fakePlayer == null) {
            fakePlayer = new FakePlayer((ServerLevel) level, new GameProfile(UUID.randomUUID(), "light-faker"));
        }
        fakePlayer.absMoveTo(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, lookingAngle, tilt);
        BlockHitResult result = level.clip(new ClipContext(startVec, endVec, Block.OUTLINE, Fluid.NONE, fakePlayer));
        BlockPos lightPos = new BlockPos(result.getLocation().x, result.getLocation().y(), result.getLocation().z());
        if (result.getType() != Type.MISS && !result.isInside()) {
            distance = result.getLocation().distanceTo(startVec);
            if (!result.getBlockPos().equals(worldPosition)) {
                lightPos = result.getBlockPos().relative(result.getDirection(), 1);
            }
        }
        if (lightPos.equals(worldPosition)) {
            return distance;
        }
        if (!level.isEmptyBlock(lightPos) && !(level
            .getBlockState(lightPos).getBlock() instanceof BlockIlluminator)) {
            lightPos = lightPos.relative(result.getDirection(), 1);
        }
        distance = new Vec3(lightPos.getX(), lightPos.getY(), lightPos.getZ()).distanceTo(new Vec3(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
        if (lightPos.equals(lightBlock)) {
            return distance;
        }
        if (getLightBlock() != null && getLightBlock() != lightPos && level.getBlockState(getLightBlock()).getBlock() instanceof BlockIlluminator) {
            level.setBlockAndUpdate(getLightBlock(), Blocks.AIR.defaultBlockState());
        }
        if ((!(level.getBlockState(lightPos).getBlock() instanceof AirBlock) && !(level.getBlockState(lightPos).getBlock() instanceof BlockIlluminator))) {
            return distance;
        }
        setLightBlock(lightPos);
        return distance;
    }

    public double getDistance() {
        return distance;
    }

    public int getPan() {
        return pan;
    }

    public int getTilt() {
        return tilt;
    }

    public int getFocus() {
        return focus;
    }

    public void setPan(int pan) {
        this.pan = pan;
        this.setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 11);
        }
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
        this.setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 11);
        }
    }

    public void setFocus(int focus) {
        this.focus = focus;
        this.setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 11);
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        readNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(getNBT(compound));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return getNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readNBT(pkt.getTag());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        readNBT(nbt);
    }

    @Override
    public HangableType getHangType() {
        if (fixture != null) {
            return fixture.getHangableType();
        }
        return HangableType.NONE;
    }

    @Override
    public ResourceLocation getStaticModel() {
        if (fixture != null) {
            net.minecraft.world.level.block.Block block = getLevel().getBlockState(worldPosition).getBlock();
            if (block instanceof BlockHangable && ((BlockHangable) block).isHanging(level, worldPosition)) {
                return getFixture().getHookedModelLocation();
            }
            return getFixture().getStaticModelLocation();
        }
        return null;
    }

    @Override
    public ResourceLocation getTiltModel() {
        if (fixture != null) {
            return getFixture().getTiltModelLocation();
        }
        return null;
    }

    @Override
    public ResourceLocation getPanModel() {
        if (fixture != null) {
            return getFixture().getPanModelLocation();
        }
        return null;
    }

    @Override
    public float[] getTiltRotationPosition() {
        if (getFixture() == null) {
            return new float[]{0, 0, 0};
        }
        return getFixture().getTiltRotationPosition();
    }

    @Override
    public float[] getPanRotationPosition() {
        if (getFixture() == null) {
            return new float[]{0, 0, 0};
        }
        return getFixture().getPanRotationPosition();
    }

    @Override
    public float getDefaultRotation() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getDefaultRotation();
    }

    @Override
    public float[] getBeamStartPosition() {
        if (getFixture() == null) {
            return new float[]{};
        }
        return getFixture().getBeamStartPosition();
    }

    @Override
    public float getBeamWidth() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getBeamWidth();
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() == null) {
            return 0;
        }
        return getFixture().getRayTraceRotation();
    }

    @Override
    public void setRemoved() {
        if(lightBlock != null) {
            if(!level.isEmptyBlock(lightBlock) && level.getBlockState(lightBlock).getBlock() instanceof BlockIlluminator) {
                level.setBlockAndUpdate(lightBlock, Blocks.AIR.defaultBlockState());
                lightBlock = null;
            }
        }
        super.setRemoved();
    }
}
