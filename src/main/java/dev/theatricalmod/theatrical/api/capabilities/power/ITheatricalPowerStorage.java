package dev.theatricalmod.theatrical.api.capabilities.power;

public interface ITheatricalPowerStorage {

    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();

}
