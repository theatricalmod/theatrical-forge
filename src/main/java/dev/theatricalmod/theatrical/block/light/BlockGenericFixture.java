package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.compat.top.ITOPInfoProvider;
import dev.theatricalmod.theatrical.items.ItemPositioner;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlockGenericFixture extends BlockLight implements ITOPInfoProvider {

    public BlockGenericFixture(Fixture fixture) {
        super(TheatricalBlocks.LIGHT_PROPERTIES, fixture);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (((TileEntityGenericFixture)level.getBlockEntity(blockPos)).getIntensity() > 0 &&
                !entity.fireImmune() && entity instanceof LivingEntity) {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(level, blockPos, blockState, entity);
    }



    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof MenuProvider) {
                if(player.getItemInHand(hand).getItem() instanceof ItemPositioner){
                    ItemStack heldItem = player.getItemInHand(hand);
                   TileEntityGenericFixture tileEntityGenericFixture = (TileEntityGenericFixture) tileEntity;
                   if(tileEntityGenericFixture.getTrackingEntity() != null){
                        tileEntityGenericFixture.setTrackingEntity(null);
                        heldItem.removeTagKey("light");
                   } else {
                        tileEntityGenericFixture.setTrackingEntity(player);
                        heldItem.getOrCreateTagElement("light").merge(NbtUtils.writeBlockPos(tileEntityGenericFixture.getBlockPos()));
                   }
                    return InteractionResult.SUCCESS;
                } else {
                    MenuProvider provider = (MenuProvider) tileEntity;
                    AbstractContainerMenu container = provider.createMenu(0, player.getInventory(), player);
                    if (container != null) {
                        if (player instanceof ServerPlayer) {
                            NetworkHooks.openGui((ServerPlayer) player, provider, buffer -> {
                                buffer.writeBlockPos(pos);
                            });
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());

        if (tileEntity instanceof TileEntityGenericFixture) {
            TileEntityGenericFixture pipe = (TileEntityGenericFixture) tileEntity;
            pipe.getCapability(TheatricalPower.CAP, null).ifPresent(iTheatricalPowerStorage -> iProbeInfo.text(new TextComponent("Power: " + iTheatricalPowerStorage.getEnergyStored())));
        }
    }

}
