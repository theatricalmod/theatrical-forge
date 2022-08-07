package dev.theatricalmod.theatrical.block;

import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockHangable extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");

    protected BlockHangable(Properties builder) {
        super(builder);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(BROKEN, false));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter p_190948_2_, List<Component> tooltips, TooltipFlag advanced) {
        if(stack.hasTag() && stack.getTag().contains("BlockStateTag") && Boolean.parseBoolean(stack.getTag().getCompound("BlockStateTag").getString("broken"))) {
            tooltips.add(new TextComponent(ChatFormatting.RED + "Broken!"));
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos blockPos, BlockState state, Player playerIn) {
        if (!world.isClientSide && playerIn.isCreative() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemStack stack = new ItemStack(this);
            CompoundTag nbt = new CompoundTag();
            if(state.getValue(BlockHangable.BROKEN)) {
                nbt.putString("broken", String.valueOf(state.getValue(BlockHangable.BROKEN)));
                stack.addTagElement("BlockStateTag", nbt);
            }
            ItemEntity itemEntity = new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
        super.playerWillDestroy(world, blockPos, state, playerIn);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BROKEN);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return isHanging(worldIn, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction from, BlockState fromState, LevelAccessor world, BlockPos pos, BlockPos fromPos) {
        world.getBlockTicks().schedule(new ScheduledTick<>(this, pos, 3, 0));
        return super.updateShape(state, from, fromState, world, pos, fromPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (!state.getValue(BROKEN) && !canSurvive(state, worldIn, pos)) {
            FallingLightEntity fallingblockentity = new FallingLightEntity(worldIn, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
            worldIn.addFreshEntity(fallingblockentity);
            //N.B. Block removal is handled in the first tick of the entity because...reasons (vanilla does it)
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public boolean isHanging(LevelReader world, BlockPos pos){
        BlockPos up = pos.above();
        return !world.isEmptyBlock(up) && world.getBlockState(up).getBlock() instanceof ISupport;
    }
}
