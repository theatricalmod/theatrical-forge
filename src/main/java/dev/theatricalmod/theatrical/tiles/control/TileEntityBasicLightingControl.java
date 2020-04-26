package dev.theatricalmod.theatrical.tiles.control;

import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public class TileEntityBasicLightingControl extends TileEntityTheatricalBase implements ITickableTileEntity {

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
    private int currentStep = 0;
    private List<StoredCue> storedSteps = new ArrayList<>();

    private boolean isRunMode = false;
    private int fadeInTicks = 0;
    private int fadeOutTicks = 0;

    private byte grandMaster = -1;

    public TileEntityBasicLightingControl() {
        super(TheatricalTiles.BASIC_LIGHTING_DESK.get());
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
            updateFaders();
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
        updateFaders();
    }

    public void updateFaders(){
        for(int i = 0; i < faders.length; i++){
            faders[i] = (byte) (convertByteToInt(faders[i]) * (convertByteToInt(grandMaster) / 255F));
        }
    }

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }
        ticks++;
        if(ticks >= 80){
            ticks = 0;
            new Random().nextBytes(faders);
            updateFaders();
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
}
