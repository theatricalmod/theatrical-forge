package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonPlug extends Button {

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/dimmer_rack.png");

    private int plugNumber;
    private String identifier;
    private boolean active;

    public ButtonPlug(int x, int y, int plugNumber, String identifier, boolean active, Button.IPressable onPress) {
        super(x, y, 14, 12, "", onPress);
        this.plugNumber = plugNumber;
        this.identifier = identifier;
        this.active = active;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(background);
        RenderSystem.disableDepthTest();
        blit(x, y, width, height, 250, 0, 19, 17, 512, 512);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x + 4, y + 5, 0);
        RenderSystem.scalef(0.5F, 0.5F, 1);
        mc.fontRenderer.drawString(identifier + plugNumber, 0, 0, 0xFFFFFF);
        RenderSystem.popMatrix();
        if (isHovered) {
            fill(x, y, x + width, y + height, -2130706433);
        }
        if (active) {
            fill(x, y, x + width, y + height, 0x6666FF66);
        }
        RenderSystem.enableDepthTest();
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
