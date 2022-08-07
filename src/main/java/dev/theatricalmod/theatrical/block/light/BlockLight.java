package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockLight extends BlockHangable implements EntityBlock {

    protected final Fixture fixture;

    protected BlockLight(Properties builder, Fixture fixture) {
        super(builder);
        this.fixture = fixture;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter p_190948_2_, List<Component> tooltips, TooltipFlag advanced) {
        if(fixture != null && advanced.isAdvanced()) {
            String[] channels = fixture.getChannelsDefinition().toString().split("#");
            for(String channel : channels) {
                tooltips.add(new TextComponent(channel));
            }
        }
        super.appendHoverText(stack, p_190948_2_, tooltips, advanced);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public Fixture getFixture() {
        return fixture;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        TileEntityFixture tileEntityFixture = this.fixture.getFixtureType().getBlockEntitySupplier().create(blockPos, blockState);
        tileEntityFixture.setFixture(fixture);
        return tileEntityFixture;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return (BlockEntityTicker<T>) this.fixture.getFixtureType().getBlockEntityTicker();
    }
}
