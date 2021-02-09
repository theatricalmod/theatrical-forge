package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.items.ItemPositioner;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockGenericFixture extends BlockHangable implements ITOPInfoProvider {

    private final Fixture fixture;

    public BlockGenericFixture(Fixture fixture) {
        super(TheatricalBlocks.LIGHT_PROPERTIES);
        this.fixture = fixture;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntityFixture tileEntityFixture = this.fixture.getFixtureType().getTileClass().get();
        tileEntityFixture.setFixture(fixture);
        return tileEntityFixture;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity ent, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote && hand == Hand.MAIN_HAND) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                if(ent.getHeldItem(hand).getItem() instanceof ItemPositioner){
                    ItemStack heldItem = ent.getHeldItem(hand);
                   TileEntityGenericFixture tileEntityGenericFixture = (TileEntityGenericFixture) tileEntity;
                   if(tileEntityGenericFixture.getTrackingEntity() != null){
                        tileEntityGenericFixture.setTrackingEntity(null);
                        heldItem.removeChildTag("light");
                   } else {
                        tileEntityGenericFixture.setTrackingEntity(ent);
                        heldItem.getOrCreateChildTag("light").merge(NBTUtil.writeBlockPos(tileEntityGenericFixture.getPos()));
                   }
                    return ActionResultType.SUCCESS;
                } else {
                    NetworkHooks.openGui((ServerPlayerEntity) ent, (INamedContainerProvider) tileEntity, tileEntity.getPos());
                }
            }
            return ActionResultType.PASS;
        }
        return super.onBlockActivated(state, world, pos, ent, hand, blockRayTraceResult);
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getTileEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityGenericFixture) {
            TileEntityGenericFixture pipe = (TileEntityGenericFixture) tileEntity;
            pipe.getCapability(TheatricalPower.CAP, null).ifPresent(iTheatricalPowerStorage -> {
                iProbeInfo.text(new StringTextComponent("Power: " + iTheatricalPowerStorage.getEnergyStored()));
            });
        }
    }
}
