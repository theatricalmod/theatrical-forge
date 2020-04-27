package dev.theatricalmod.theatrical.tiles.control;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityBasicLightingControl extends TileEntityTheatricalBase implements ITickableTileEntity, INamedContainerProvider, IAcceptsCable {


    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{
                CableType.DMX
        };
    }

    class StoredCue {
        private byte[] faders;
        private int fadeInTicks;
        private int fadeOutTicks;

        public StoredCue(){}

        public StoredCue(byte[] faders, int fadeInTicks, int fadeOutTicks){
            this.faders = faders;
            this.fadeInTicks = fadeInTicks;
            this.fadeOutTicks = fadeOutTicks;
        }

        public CompoundNBT toNBT(){
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putByteArray("faders", faders);
            compoundNBT.putInt("fadeIn", fadeInTicks);
            compoundNBT.putInt("fadeOut", fadeOutTicks);
            return compoundNBT;
        }

        public StoredCue fromNBT(CompoundNBT nbt){
            this.faders = nbt.getByteArray("faders");
            this.fadeInTicks = nbt.getInt("fadeIn");
            this.fadeOutTicks = nbt.getInt("fadeOut");
            return this;
        }


        public byte[] getFaders() {
            return faders;
        }

        public int getFadeInTicks() {
            return fadeInTicks;
        }

        public int getFadeOutTicks() {
            return fadeOutTicks;
        }
    }


    private int ticks = 0;
    private byte[] faders = new byte[12];
    private byte[] actualDMX = new byte[12];
    private int currentStep = 0;
    private List<StoredCue> storedSteps = new ArrayList<>();

    private boolean isRunMode = false;
    private int fadeInTicks = 0;
    private int fadeOutTicks = 0;

    private byte grandMaster = -1;

    private IDMXProvider idmxProvider;

    public TileEntityBasicLightingControl() {
        super(TheatricalTiles.BASIC_LIGHTING_DESK.get());
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    public float convertByteToInt(byte val) {
        return val & 0xFF;
    }

    public byte[] getFaders() {
        return faders;
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        if(nbt.contains("faders")){
            faders = nbt.getByteArray("faders");
        }
        if(nbt.contains("storedSteps")){
            storedSteps = new ArrayList<>();
            CompoundNBT compoundNBT = nbt.getCompound("storedSteps");
            int length = compoundNBT.getInt("length");
            for(int i = 0; i < length; i++){
                storedSteps.add(new StoredCue().fromNBT(compoundNBT.getCompound("step_" + i)));
            }
        }
        if(nbt.contains("currentStep")){
            currentStep = nbt.getInt("currentStep");
        }
        if(nbt.contains("grandMaster")){
            grandMaster = nbt.getByte("grandMaster");
        }
        if(nbt.contains("isRunMode")){
            isRunMode = nbt.getBoolean("isRunMode");
        }
    }

    @Override
    public CompoundNBT getNBT(CompoundNBT nbt) {
        if(nbt == null){
            nbt = new CompoundNBT();
        }
        nbt.putByteArray("faders", faders);
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("length", storedSteps.size());
        for(int i = 0; i < storedSteps.size(); i++){
            compoundNBT.put("step_" + i, storedSteps.get(i).toNBT());
        }
        nbt.put("storedSteps", compoundNBT);
        nbt.putInt("currentStep", currentStep);
        nbt.putByte("grandMaster", grandMaster);
        nbt.putBoolean("isRunMode", isRunMode);
        return super.getNBT(nbt);
    }

    public void setFaders(byte[] faders){
        this.faders = faders;
    }

    public void setFader(int fader, int value){
        if(fader != -1) {
            this.faders[fader] = (byte) value;
        } else {
            this.grandMaster = (byte) value;
        }
    }

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }
        ticks++;
        if(ticks >= 1){
            ticks = 0;
            byte[] dmx = new byte[512];
            for(int i = 0; i < faders.length; i++){
                dmx[i] = (byte) (convertByteToInt(faders[i]) * (convertByteToInt(grandMaster) / 255F));
            }
            this.idmxProvider.getUniverse(world).setDmxChannels(dmx);
            Dimension dimension = world.dimension;
            TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(dimension::getType), new SendDMXProviderPacket(pos, dmx));
            sendDMXSignal();
            markDirty();
        }
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public byte getGrandMaster() {
        return grandMaster;
    }

    public boolean isRunMode() {
        return false;
    }

    public void clickButton(){
        if(isRunMode()){
            this.recallNextStep();
        } else {
            this.storeCurrentFaders();
        }
    }

    private void recallNextStep(){
        if(this.storedSteps.size() < this.currentStep){
            return;
        }
        this.currentStep++;
        byte[] faders = storedSteps.get(this.currentStep).getFaders();
        setFaders(faders);
    }

    private void storeCurrentFaders(){
        this.currentStep++;
        StoredCue storedCue = new StoredCue(this.faders, this.fadeInTicks, this.fadeOutTicks);
        storedSteps.add(storedCue);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Basic Lighting Control");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new ContainerBasicLightingConsole(i, world, getPos());
    }

    @Override
    public void remove() {
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.remove();
    }

    @Override
    public void setWorldAndPos(World p_226984_1_, BlockPos p_226984_2_) {
        super.setWorldAndPos(p_226984_1_, p_226984_2_);
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
    }

    public void sendDMXSignal() {
        idmxProvider.updateDevices(world, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction direction) {
        if (cap == DMXProvider.CAP) {
            return DMXProvider.CAP.orEmpty(cap, LazyOptional.of(() -> idmxProvider));
        }
        return super.getCapability(cap, direction);
    }

}
