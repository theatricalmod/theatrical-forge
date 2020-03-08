package dev.theatricalmod.theatrical.block.light;

import static dev.theatricalmod.theatrical.block.light.IntelligentFixtureBlock.FLIPPED;

import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.block.DMXAcceptorBlockEntity;
import dev.theatricalmod.theatrical.block.TheatricalBlockEntities;
import dev.theatricalmod.theatrical.client.gui.container.IntelligentFixtureContainer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IntelligentFixtureBlockEntity extends DMXAcceptorBlockEntity implements IRGB, INamedContainerProvider {


    public IntelligentFixtureBlockEntity() {
        super(TheatricalBlockEntities.MOVING_LIGHT);
    }

    @Override
    public void setFixture(Fixture fixture) {
        super.setFixture(fixture);
        if (fixture != null) {
            this.getIdmxReceiver().setChannelCount(fixture.getChannelCount());
        }
    }

    @Override
    public float getMaxLightDistance() {
        return 10;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return MovingLightBlock.class;
    }

    @Override
    public boolean shouldTrace() {
        return true;
    }

    @Override
    public boolean emitsLight() {
        return true;
    }

    @Override
    public boolean isUpsideDown() {
        return world.getBlockState(pos).get(IntelligentFixtureBlock.FLIPPED);
    }

    @Override
    public float getDefaultRotation() {
        return 90F;
    }

    @Override
    public float getBeamWidth() {
        return 0.15F;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0.5F, 0.5F, 0.15F};
    }

    public int getColorHex() {
        return (getRed() << 16) | (getGreen() << 8) | getBlue();
    }

    public int getRed() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.RED)))).orElse(0);
        }
        return 0;
    }

    public int getGreen() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.GREEN)))).orElse(0);
        }
        return 0;
    }

    public int getBlue() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.BLUE)))).orElse(0);
        }
        return 0;
    }

    @Override
    public int getPan() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int)((convertByteToInt(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.PAN))).orElse((byte)prevPan)) * 360) / 255F);        }
        return prevPan;
    }

    @Override
    public int getTilt() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int)((convertByteToInt(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.TILT))).orElse((byte)prevTilt)) * 180) / 255F) - 90;
        }
        return prevTilt;
    }

    @Override
    public int getFocus() {
        return 6;
//        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
//            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.FOCUS))).orElse((byte)prevFocus);
//        }
//        return prevFocus;
    }

    @Override
    public float getIntensity() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByte(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.INTENSITY))).orElse((byte)0));
        }
        return 0;
    }



    public float convertByte(byte val) {
        return val & 0xFF;
    }

    public int convertByteToInt(byte val) {
        return val & 0xFF;
    }

    @Override
    public void tick() {
        super.tick();
        prevPan = getPan();
        prevTilt= getTilt();
        prevFocus = getFocus();
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() != null) {
            return world.getBlockState(pos).get(FLIPPED) ? getFixture().getRayTraceRotation() : 0;
        }
        return 0;
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Intelligent Fixture");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new IntelligentFixtureContainer(i, getWorld(), getPos());
    }
}
