package dev.theatricalmod.theatrical.tiles.control;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;

import java.util.*;
import java.util.stream.Stream;

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

//    speed = difference between two states / ticks
//    distance = difference between two states
//    time = ticks

    private int ticks = 0;
    private byte[] faders = new byte[12];
    private byte[] actualDMX = new byte[12];
    private int currentStep = 0;
    private HashMap<Integer, StoredCue> storedSteps = new HashMap<>();
    private StoredCue activeCue;

    private boolean isRunMode = false;
    private int fadeInTicks = 0;
    private int fadeOutTicks = 0;

    private int fadeInTicksRemaining = 0;
    private int fadeOutTicksRemaining = 0;
    private byte[] perTickOut, perTickIn;
    private boolean isFadingOut = false;

    private byte grandMaster = -1;

    private IDMXProvider idmxProvider;

    public TileEntityBasicLightingControl() {
        super(TheatricalTiles.BASIC_LIGHTING_DESK.get());
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    public float convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    public byte[] getFaders() {
        return this.faders;
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        if(nbt.contains("faders")){
            faders = nbt.getByteArray("faders");
        }
        if(nbt.contains("storedSteps")){
            storedSteps = new HashMap<>();
            CompoundNBT compoundNBT = nbt.getCompound("storedSteps");
            for(String key : compoundNBT.keySet()){
                int stepNumber = Integer.parseInt(key);
                storedSteps.put(stepNumber, new StoredCue().fromNBT(compoundNBT.getCompound(key)));
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
        if(nbt.contains("fadeInTicks")){
            fadeInTicks = nbt.getInt("fadeInTicks");
        }
        if(nbt.contains("fadeOutTicks")){
            fadeOutTicks = nbt.getInt("fadeOutTicks");
        }
    }

    @Override
    public CompoundNBT getNBT(CompoundNBT nbt) {
        if(nbt == null){
            nbt = new CompoundNBT();
        }
        nbt.putByteArray("faders", faders);
        CompoundNBT compoundNBT = new CompoundNBT();
        for(int key : storedSteps.keySet()){
            compoundNBT.put(Integer.toString(key), storedSteps.get(key).toNBT());
        }
        nbt.put("storedSteps", compoundNBT);
        nbt.putInt("currentStep", currentStep);
        nbt.putByte("grandMaster", grandMaster);
        nbt.putBoolean("isRunMode", isRunMode);
        nbt.putInt("fadeInTicks", fadeInTicks);
        nbt.putInt("fadeOutTicks", fadeOutTicks);
        return super.getNBT(nbt);
    }

    public void setFaders(byte[] faders){
        this.faders = Arrays.copyOf(faders, faders.length);
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
            if(isFadingOut){
                if(fadeOutTicksRemaining > 0) {
                    fadeOutTicksRemaining--;
                    this.doFadeTickOut();
                } else {
                    isFadingOut = false;
                }
            } else {
                if(fadeInTicksRemaining > 0){
                    fadeInTicksRemaining--;
                    this.doFadeTickIn();
                }
            }
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
        return isRunMode;
    }

    public void toggleMode(){
        this.isRunMode = !this.isRunMode;
    }

    public void clickButton(){
        if(isRunMode()){
            this.recallNextStep();
        } else {
            this.storeCurrentFaders();
        }
    }

    public void moveForward(){
        if(isRunMode){
            this.currentStep = this.getNextStep();
        } else {
            this.currentStep++;
            if(this.storedSteps.containsKey(this.currentStep)){
                byte[] faders = storedSteps.get(this.currentStep).getFaders();
                setFaders(faders);
            }
        }
    }

    public void moveBack(){
        if(this.isRunMode){
            this.currentStep = this.getPreviousStep();
        } else {
            if(this.currentStep - 1 < 0){
                return;
            }
            this.currentStep--;
            if(this.storedSteps.containsKey(this.currentStep)){
                byte[] faders = storedSteps.get(this.currentStep).getFaders();
                setFaders(faders);
            }
        }
    }

    public HashMap<Integer, StoredCue> getStoredSteps() {
        return storedSteps;
    }

    private void doFadeTickIn(){
        for(int i = 0; i < faders.length; i++) {
            this.faders[i] = (byte)(faders[i] - perTickIn[i]);
        }
    }
    private void doFadeTickOut(){
        for(int i = 0; i < faders.length; i++) {
            this.faders[i] = (byte)(faders[i] - perTickOut[i]);
        }
    }

    private void recallNextStep(){
        if(this.storedSteps.size() < this.currentStep){
            return;
        }
        StoredCue previousCue = activeCue;
        if(!this.storedSteps.containsKey(this.currentStep)){
            return;
        }
        StoredCue storedCue = storedSteps.get(this.currentStep);
        if(previousCue != null) {
            if (previousCue.fadeOutTicks > 0) {
                this.isFadingOut = true;
                this.fadeOutTicksRemaining = previousCue.getFadeOutTicks();
                this.perTickOut = new byte[12];
                for (int i = 0; i < faders.length; i++) {
                    this.perTickOut[i] = (byte)((convertByteToInt(faders[i])) / fadeOutTicksRemaining);
                }
            }
        }
        if(storedCue.fadeInTicks > 0){
            this.fadeInTicksRemaining = storedCue.getFadeInTicks();
            this.perTickIn = new byte[12];
            for(int i = 0; i < faders.length; i++) {
                if(isFadingOut){
                    this.perTickIn[i] = (byte)(-convertByteToInt(storedCue.getFaders()[i]) / fadeInTicksRemaining);
                } else {
                    this.perTickIn[i] = (byte)((convertByteToInt(faders[i]) - convertByteToInt(storedCue.getFaders()[i])) / fadeInTicksRemaining);
                }
            }
        } else {
            byte[] faders = storedCue.getFaders();
            setFaders(faders);
        }
        activeCue = storedCue;
        this.currentStep = getNextStep();
        markDirty();
    }

    private Integer getFirst(){
        return this.storedSteps.keySet().stream().sorted(Integer::compareTo).findFirst().get();
    }

    private Integer getNextStep(){
        Optional<Integer> nextSteps = this.storedSteps.keySet().stream().filter(integer -> integer > this.currentStep).sorted(Integer::compareTo).findFirst();
        return nextSteps.orElseGet(this::getFirst);
    }

    private Integer getPreviousStep(){
        Optional<Integer> nextSteps = this.storedSteps.keySet().stream().filter(integer -> integer < this.currentStep).sorted(Integer::compareTo).sorted(Collections.reverseOrder()).findFirst();
        return nextSteps.orElseGet(() -> this.storedSteps.keySet().stream().sorted(Integer::compareTo).sorted(Collections.reverseOrder()).findFirst().get());
    }

    private void storeCurrentFaders(){
        StoredCue storedCue = new StoredCue(Arrays.copyOf(faders, faders.length), this.fadeInTicks, this.fadeOutTicks);
        storedSteps.put(this.currentStep, storedCue);
        this.currentStep++;
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


    public int getFadeInTicks() {
        return fadeInTicks;
    }

    public void setFadeInTicks(int fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
    }

    public int getFadeOutTicks() {
        return fadeOutTicks;
    }

    public void setFadeOutTicks(int fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
    }
}
