package dev.theatricalmod.theatrical.tiles.control;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.capability.CapabilityDMXProvider;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerBasicLightingConsole;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class TileEntityBasicLightingControl extends TileEntityTheatricalBase implements MenuProvider, IAcceptsCable {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityBasicLightingControl tile = (TileEntityBasicLightingControl) be;
        if(level.isClientSide){
            return;
        }
        tile.ticks++;
        if(tile.ticks >= 1){
            if(tile.isFadingOut){
                if(tile.fadeOutTicksRemaining > 0) {
                    tile.fadeOutTicksRemaining--;
                    tile.doFadeTickOut();
                } else {
                    tile.isFadingOut = false;
                }
            } else {
                if(tile.fadeInTicksRemaining > 0){
                    tile.fadeInTicksRemaining--;
                    tile.doFadeTickIn();
                }
            }
            tile.ticks = 0;
            byte[] dmx = new byte[512];
            for(int i = 0; i < tile.faders.length; i++){
                dmx[i] = (byte) (tile.convertByteToInt(tile.faders[i]) * (tile.convertByteToInt(tile.grandMaster) / 255F));
            }
            tile.dmxProvider.getUniverse(level).setDmxChannels(dmx);
            TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(level::dimension), new SendDMXProviderPacket(pos, dmx));
            tile.sendDMXSignal();
            tile.setChanged();
        }
    }

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

        public CompoundTag toNBT(){
            CompoundTag compoundNBT = new CompoundTag();
            compoundNBT.putByteArray("faders", faders);
            compoundNBT.putInt("fadeIn", fadeInTicks);
            compoundNBT.putInt("fadeOut", fadeOutTicks);
            return compoundNBT;
        }

        public StoredCue fromNBT(CompoundTag nbt){
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
    private final byte[] actualDMX = new byte[12];
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

    private final CapabilityDMXProvider dmxProvider;

    public TileEntityBasicLightingControl(BlockPos pos, BlockState state) {
        super(TheatricalTiles.BASIC_LIGHTING_DESK.get(), pos, state);
        this.dmxProvider = new CapabilityDMXProvider(new DMXUniverse());
    }

    public float convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    public byte[] getFaders() {
        return this.faders;
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        if(nbt.contains("faders")){
            faders = nbt.getByteArray("faders");
        }
        if(nbt.contains("storedSteps")){
            storedSteps = new HashMap<>();
            CompoundTag compoundNBT = nbt.getCompound("storedSteps");
            for(String key : compoundNBT.getAllKeys()){
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
    public CompoundTag getNBT(CompoundTag nbt) {
        if(nbt == null){
            nbt = new CompoundTag();
        }
        nbt.putByteArray("faders", faders);
        CompoundTag compoundNBT = new CompoundTag();
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
        setChanged();
    }

    private Integer getFirst(){
        return this.storedSteps.keySet().stream().min(Integer::compareTo).get();
    }

    private Integer getNextStep(){
        if(this.storedSteps.size() > 0) {
            Optional<Integer> nextSteps = this.storedSteps.keySet().stream().filter(integer -> integer > this.currentStep).min(Integer::compareTo);
            return nextSteps.orElseGet(this::getFirst);
        } else {
            return currentStep;
        }
    }

    private Integer getPreviousStep(){
        if(this.storedSteps.size() > 0) {
            Optional<Integer> nextSteps = this.storedSteps.keySet().stream().filter(integer -> integer < this.currentStep).sorted(Integer::compareTo).max(Comparator.naturalOrder());
            return nextSteps.orElseGet(() -> this.storedSteps.keySet().stream().sorted(Integer::compareTo).max(Comparator.naturalOrder()).get());
        } else {
            return currentStep;
        }
    }

    private void storeCurrentFaders(){
        StoredCue storedCue = new StoredCue(Arrays.copyOf(faders, faders.length), this.fadeInTicks, this.fadeOutTicks);
        storedSteps.put(this.currentStep, storedCue);
        this.currentStep++;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Basic Lighting Control");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new ContainerBasicLightingConsole(i, level, getBlockPos());
    }

    public void sendDMXSignal() {
        dmxProvider.updateDevices(level, worldPosition);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction direction) {
        if (cap == TheatricalCapabilities.CAPABILITY_DMX_PROVIDER) {
            return TheatricalCapabilities.CAPABILITY_DMX_PROVIDER.orEmpty(cap, LazyOptional.of(() -> dmxProvider));
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
