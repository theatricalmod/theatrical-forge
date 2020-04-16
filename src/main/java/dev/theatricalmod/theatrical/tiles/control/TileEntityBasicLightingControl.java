package dev.theatricalmod.theatrical.tiles.control;

import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

public class TileEntityBasicLightingControl extends TileEntityTheatricalBase implements ITickableTileEntity {


    private int ticks = 0;
    private byte[] faders = new byte[12];
    private int currentStep = 0;
    private List<byte[]> storedSteps = new ArrayList<>();

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
                storedSteps.add(compoundNBT.getByteArray("step_" + i));
            }
        }
        if(nbt.contains("currentStep")){
            currentStep = nbt.getInt("currentStep");
        }
        if(nbt.contains("grandMaster")){
            grandMaster = nbt.getByte("grandMaster");
            updateFaders();
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
            compoundNBT.putByteArray("step_" + i, storedSteps.get(i));
        }
        nbt.put("storedSteps", compoundNBT);
        nbt.putInt("currentStep", currentStep);
        nbt.putByte("grandMaster", grandMaster);
        return super.getNBT(nbt);
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
}
