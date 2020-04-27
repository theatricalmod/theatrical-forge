package dev.theatricalmod.theatrical.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.client.gui.IDraggable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class ButtonFader extends Widget{

    private static final ResourceLocation background = new ResourceLocation(TheatricalMod.MOD_ID,
        "textures/gui/lighting_console.png");

    private int channel;
    private int value;

    private boolean dragging = false;

    public IDraggable onDrag;

    public ButtonFader(int x, int y, int channel, int value, IDraggable draggable) {
        super(x, y, 10, 51, "");
        this.channel = channel;
        this.value = value;
        this.onDrag = draggable;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(background);
        RenderSystem.disableDepthTest();
        blit(x, y, width, height, 176, 0, 10, 51, 256, 256);
        blit(x + 1, (y + (height - 7)) - (int) ((this.value / 255f) * 50), 8, 11, 186, 0, 8, 11, 256, 256);
        RenderSystem.enableDepthTest();
    }

    public int getChannel() {
        return channel;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.value = (int) (((this.height - (mouseY - this.y)) / this.height) * 255f);
        this.dragging = true;
    }

    @Override
    public void onRelease(double p_onRelease_1_, double p_onRelease_3_) {
        this.dragging = false;
    }

    public int calculateNewValue(double mouseY){
        return (int) (((this.height - (mouseY - this.y)) / this.height) * 255f);
    }

    public boolean isDragging(){
        return this.dragging;
    }
}
