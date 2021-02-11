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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.Random;

public class BlockGenericFixture extends BlockLight implements ITOPInfoProvider {

    public BlockGenericFixture(Fixture fixture) {
        super(TheatricalBlocks.LIGHT_PROPERTIES, fixture);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (((TileEntityGenericFixture)world.getTileEntity(pos)).getIntensity() > 0 &&
                !entity.isImmuneToFire() && entity instanceof LivingEntity) {
            entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.onEntityWalk(world, pos, entity);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote && hand == Hand.MAIN_HAND) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof INamedContainerProvider) {
                if(player.getHeldItem(hand).getItem() instanceof ItemPositioner){
                    ItemStack heldItem = player.getHeldItem(hand);
                   TileEntityGenericFixture tileEntityGenericFixture = (TileEntityGenericFixture) tileEntity;
                   if(tileEntityGenericFixture.getTrackingEntity() != null){
                        tileEntityGenericFixture.setTrackingEntity(null);
                        heldItem.removeChildTag("light");
                   } else {
                        tileEntityGenericFixture.setTrackingEntity(player);
                        heldItem.getOrCreateChildTag("light").merge(NBTUtil.writeBlockPos(tileEntityGenericFixture.getPos()));
                   }
                    return ActionResultType.SUCCESS;
                } else {
                    INamedContainerProvider provider = (INamedContainerProvider) tileEntity;
                    Container container = provider.createMenu(0, player.inventory, player);
                    if (container != null) {
                        if (player instanceof ServerPlayerEntity) {
                            NetworkHooks.openGui((ServerPlayerEntity) player, provider, buffer -> {
                                buffer.writeBlockPos(pos);
                            });
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            return ActionResultType.PASS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, blockRayTraceResult);
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
