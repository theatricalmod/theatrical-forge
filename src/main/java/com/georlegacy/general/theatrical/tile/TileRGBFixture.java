package com.georlegacy.general.theatrical.tile;

import com.georlegacy.general.theatrical.api.IRGB;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileRGBFixture extends TileFixture implements IRGB {

    private int red, green, blue = 0;

    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("red", red);
        nbtTagCompound.setInteger("green", green);
        nbtTagCompound.setInteger("blue", blue);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        red = nbtTagCompound.getInteger("red");
        green = nbtTagCompound.getInteger("green");
        blue = nbtTagCompound.getInteger("blue");
    }

    @Override
    public int getColorHex() {
        return (red << 16) | (green << 8) | blue;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}
