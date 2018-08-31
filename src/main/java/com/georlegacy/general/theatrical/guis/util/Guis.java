package com.georlegacy.general.theatrical.guis.util;
import net.minecraft.client.Minecraft;

public final class Guis {

    private Guis() { }

    public static void close() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    public static boolean isPointInRegion(int x, int y, int width, int height, int pointX, int pointY) {
        return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
    }

    public static int computeGuiScale() {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;

        int k = mc.gameSettings.guiScale;

        if (k == 0) k = 1000;

        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        return scaleFactor;
    }

}
