package com.georlegacy.general.theatrical.api.capabilities.power.bundled;

public interface IBundledTheatricalPowerStorage {

    int[] receiveEnergy(int[] channels, boolean simulate);

    int[] extractEnergy(int[] channels, boolean simulate);

    int getEnergyStored(int channel);

    int getMaxEnergyStored(int channel);

    boolean canExtract(int channel);

    boolean canReceive(int channel);

}
