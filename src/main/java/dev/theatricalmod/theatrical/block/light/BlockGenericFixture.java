package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import dev.theatricalmod.theatrical.items.ItemPositioner;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockGenericFixture extends BlockLight implements ITOPInfoProvider {

    public BlockGenericFixture(Fixture fixture) {
        super(TheatricalBlocks.LIGHT_PROPERTIES, fixture);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction from, BlockState fromState, IWorld world, BlockPos pos, BlockPos fromPos) {
        if (world instanceof World && !isValidPosition(state, world, pos)) {
            FallingLightEntity fallingblockentity = new FallingLightEntity((World) world, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, world.getBlockState(pos));
            world.addEntity(fallingblockentity);
            //N.B. Block removal is handled in the first tick of the entity because...reasons (vanilla does it)
        }
        return super.updatePostPlacement(state, from, fromState, world, pos, fromPos);
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
            pipe.getCapability(TheatricalPower.CAP, null).ifPresent(iTheatricalPowerStorage -> iProbeInfo.text(new StringTextComponent("Power: " + iTheatricalPowerStorage.getEnergyStored())));
        }
    }
}
