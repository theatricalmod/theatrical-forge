package dev.theatricalmod.theatrical.block.control;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BlockBasicLightingControl extends DirectionalBlock implements EntityBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final VoxelShape shape = Shapes.box(0, 0, 0, 16 / 16D, 3 / 16D, 16 / 16D);

    public BlockBasicLightingControl() {
        super(TheatricalBlocks.BASE_PROPERTIES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, p_196258_1_.getHorizontalDirection().getOpposite()).setValue(POWERED, false);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, POWERED);
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public boolean causesSuffocation(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return false;
    }

    public boolean isNormalCube(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return false;
    }

    public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return shape;
    }



    @Override
    public InteractionResult use(BlockState p_225533_1_, Level world, BlockPos pos, Player player, InteractionHand p_225533_5_, BlockHitResult p_225533_6_) {
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
        return super.use(p_225533_1_, world, pos, player, p_225533_5_, p_225533_6_);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            boolean flag = worldIn.hasNeighborSignal(pos);
            if (flag != state.getValue(POWERED)) {
                if(flag){
                    TileEntityBasicLightingControl tileEntityBasicLightingControl = (TileEntityBasicLightingControl) worldIn.getBlockEntity(pos);
                    tileEntityBasicLightingControl.clickButton();
                }
                worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag)), 2);
            }

        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityBasicLightingControl(pos, state);
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> type) {
        return type == TheatricalTiles.BASIC_LIGHTING_DESK.get() ? TileEntityBasicLightingControl::tick : null;
    }
}
