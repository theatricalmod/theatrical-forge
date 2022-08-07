package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockIntelligentFixture extends BlockLight {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

    public BlockIntelligentFixture(Fixture fixture, Properties builder) {
        super(builder, fixture);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(HANGING, context.getClickedFace() == Direction.DOWN || isHanging(context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        if(state.getValue(HANGING)){
            return isHanging(worldIn, pos);
        }
        return !worldIn.getBlockState(pos.relative(Direction.DOWN)).isAir();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof MenuProvider) {
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
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

}
