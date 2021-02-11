package dev.theatricalmod.theatrical.tiles.lights;

import com.mojang.authlib.GameProfile;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.api.fixtures.IFixture;
import dev.theatricalmod.theatrical.api.fixtures.IFixtureModelProvider;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.block.light.BlockIlluminator;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class TileEntityFixture extends TileEntity implements IFixture, ITickableTileEntity, IFixtureModelProvider {

    private Fixture fixture;
    private double distance = 0;
    private int pan, tilt = 0;
    private int focus = 6;

    private long timer = 0;

    public int prevTilt, prevPan, prevFocus = 0;

    private BlockPos lightBlock;

    private FakePlayer fakePlayer;

    public TileEntityFixture(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
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
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public CompoundNBT getNBT(@Nullable CompoundNBT compoundNBT) {
        if (compoundNBT == null) {
            compoundNBT = new CompoundNBT();
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

    public void readNBT(CompoundNBT compoundNBT) {
        pan = compoundNBT.getInt("pan");
        tilt = compoundNBT.getInt("tilt");
        focus = compoundNBT.getInt("focus");
        prevPan = compoundNBT.getInt("prevPan");
        prevTilt = compoundNBT.getInt("prevTilt");
        prevFocus = compoundNBT.getInt("prevFocus");
        timer = compoundNBT.getLong("timer");
        distance = compoundNBT.getDouble("distance");
        if (compoundNBT.contains("fixture_type")) {
            setFixture(Fixture.getRegistry().getValue(new ResourceLocation(compoundNBT.getString("fixture_type"))));
        } else {
            setFixture(null);
        }
    }

    public int getExtraTilt(){
        return 0;
    }

    public final Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d(f3 * f4, -f5, f2 * f4);
    }

    @Override
    public boolean emitsLight() {
        return !getBlockState().get(BlockHangable.BROKEN);
    }

    public double doRayTrace() {
//        if (!this.getBlock().isInstance(world.getBlockState(pos).getBlock())) {
//            return 0;
//        }
        BlockState blockState = world.getBlockState(pos);
        Direction direction = blockState.get(
            HorizontalBlock.HORIZONTAL_FACING);
        float horizontalAngle = direction.getHorizontalAngle();
        float lookingAngle = horizontalAngle;
        lookingAngle = (isUpsideDown() ? lookingAngle + getPan() : lookingAngle - getPan());
        lookingAngle = lookingAngle % 360;
        if (isUpsideDown()) {
//            lookingAngle = 360 - lookingAngle;
//            lookingAngle = lookingAngle % 360;
        }

        float tilt = getTilt() + getExtraTilt();
        if (!isUpsideDown()) {
            tilt = -tilt;
        }
//        tilt = (tilt / 2) + 90;
//        if(isUpsideDown()){
//            lookingAngle = -lookingAngle;
//        }
        Vector3d look = getVectorForRotation(tilt, lookingAngle);
        double distance = getMaxLightDistance();
        Vector3d startVec = look.scale(0.9F).add(pos.getX() + 0.5, pos.getY() + 0.51, pos.getZ() + 0.5);
        Vector3d endVec = look.scale(distance).add(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        world.addParticle(RedstoneParticleData.REDSTONE_DUST, endVec.x, endVec.y, endVec.z, 0, 0, 0);
        if (fakePlayer == null) {
            fakePlayer = new FakePlayer((ServerWorld) world, new GameProfile(UUID.randomUUID(), "light-faker"));
        }
        fakePlayer.setPositionAndRotation(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, lookingAngle, tilt);
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(startVec, endVec, BlockMode.OUTLINE, FluidMode.NONE, fakePlayer));
        BlockPos lightPos = new BlockPos(result.getHitVec().x, result.getHitVec().getY(), result.getHitVec().getZ());
        if (result.getType() != Type.MISS && !result.isInside()) {
            distance = result.getHitVec().distanceTo(startVec);
            if (!result.getPos().equals(pos)) {
                lightPos = result.getPos().offset(result.getFace(), 1);
            }
        }
        if (lightPos.equals(pos)) {
            return distance;
        }
        if (!world.isAirBlock(lightPos) && !(world
            .getBlockState(lightPos).getBlock() instanceof BlockIlluminator)) {
            lightPos = lightPos.offset(result.getFace(), 1);
        }
        distance = new Vector3d(lightPos.getX(), lightPos.getY(), lightPos.getZ()).distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
        if (lightPos.equals(lightBlock)) {
            if (world.getBlockState(lightBlock).getBlock() instanceof AirBlock) {
                if (this.emitsLight()) {
                    float newVal = getIntensity() / 255F;
                    int lightval = (int) (newVal * 15F);
                    world.setBlockState(lightPos,
                        TheatricalBlocks.ILLUMINATOR.get().getDefaultState().with(BlockIlluminator.lightValue, lightval), 3);
                    TileEntity tileEntity = world.getTileEntity(lightPos);
                    if (tileEntity != null) {
                        TileEntityIlluminator illuminator = (TileEntityIlluminator) tileEntity;
                        illuminator.setController(pos);
//                        if (world.isRemote) {
//                            TheatricalPacketHandler.INSTANCE
//                                .sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
//                        }
                    }
                }
            }
            return distance;
        }
        if (getLightBlock() != null && getLightBlock() != lightPos && world.getBlockState(getLightBlock()).getBlock() instanceof BlockIlluminator) {
            world.setBlockState(getLightBlock(), Blocks.AIR.getDefaultState());
        }
        if ((!(world.getBlockState(lightPos).getBlock() instanceof AirBlock) && !(world.getBlockState(lightPos).getBlock() instanceof BlockIlluminator))) {
            return distance;
        }
        setLightBlock(lightPos);
        if (this.emitsLight()) {
            float newVal = getIntensity() / 255F;
            int lightval = (int) (newVal * 15F);
            world.setBlockState(lightPos,
                TheatricalBlocks.ILLUMINATOR.get().getDefaultState().with(BlockIlluminator.lightValue, lightval), 3);
            TileEntity tileEntity = world.getTileEntity(lightPos);
            if (tileEntity != null) {
                TileEntityIlluminator illuminator = (TileEntityIlluminator) tileEntity;
                illuminator.setController(pos);
                if (lightBlock != null) {
                    world.getLightValue(lightBlock);
                }
//                if (world.isRemote) {
//                    TheatricalPacketHandler.INSTANCE
//                        .sendToServer(new UpdateIlluminatorPacket(lightPos, pos));
//                }
            }
        }
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
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    public void setFocus(int focus) {
        this.focus = focus;
        this.markDirty();
        if (!world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        readNBT(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(getNBT(compound));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, getNBT(null));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return getNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readNBT(pkt.getNbtCompound());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
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
            Block block = getWorld().getBlockState(pos).getBlock();
            if (block instanceof BlockHangable && ((BlockHangable) block).isHanging(world, pos)) {
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
    public void tick() {
        prevFocus = focus;
        prevPan = pan;
        prevTilt = tilt;
        if (!world.isRemote) {
            timer++;
            if (timer >= 5) {
                if (shouldTrace()) {
                    this.distance = doRayTrace();
                    world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 11);
                    markDirty();
                    if (lightBlock != null) {
                        if (!world.getBlockState(lightBlock).isAir() && world.getBlockState(lightBlock).getBlock() instanceof BlockIlluminator) {
                            float newVal = getIntensity() / 255F;
                            int lightval = (int) (newVal * 15F);
                            world.setBlockState(lightBlock, world.getBlockState(lightBlock).with(BlockIlluminator.lightValue, lightval));
                        }
                    }
                }
                timer = 0;
            }
        }
    }
}
