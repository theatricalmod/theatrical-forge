package dev.theatricalmod.theatrical.guis.dimming;

import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.guis.widgets.ButtonPlug;
import dev.theatricalmod.theatrical.guis.widgets.ButtonSocket;
import dev.theatricalmod.theatrical.handlers.TheatricalPacketHandler;
import dev.theatricalmod.theatrical.init.TheatricalBlocks;
import dev.theatricalmod.theatrical.packets.ChangeDimmerPatchPacket;
import dev.theatricalmod.theatrical.packets.UpdateDMXStartAddressPacket;
import dev.theatricalmod.theatrical.tiles.dimming.TileDimmerRack;
import dev.theatricalmod.theatrical.util.Reference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDimmerRack extends GuiContainer {

    private static final ResourceLocation background = new ResourceLocation(Reference.MOD_ID,
        "textures/gui/dimmer_rack.png");

    private ContainerDimmerRack inventoryPlayer;
    private TileDimmerRack tileDimmerRack;
    private List<ButtonSocket> sockets;
    private List<ButtonPlug> plugs;
    private List<ISocapexReceiver> receivers;
    private GuiTextField dmxStartField;
    private int currentPage = 0;

    private int activePlug = -1;

    public GuiDimmerRack(TileDimmerRack tileDimmerRack, ContainerDimmerRack inventorySlotsIn) {
        super(inventorySlotsIn);
        this.inventoryPlayer = inventorySlotsIn;
        this.tileDimmerRack = tileDimmerRack;

        this.xSize = 250;
        this.ySize = 203;
        sockets = new ArrayList<>();
        plugs = new ArrayList<>();
        receivers = inventoryPlayer.getDevices();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, 512, 512);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = I18n.format(TheatricalBlocks.BLOCK_DIMMER_RACK.getTranslationKey() + ".name");
        fontRenderer
            .drawString(name, 176 / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer
            .drawString("Plugs", 180 + fontRenderer.getStringWidth("Plugs") / 2, 6, 0x404040);
        fontRenderer
            .drawString(inventoryPlayer.getPlayerInventory().getDisplayName().getUnformattedText(),
                8, ySize - 94, 0x404040);
        for (int i = 0; i < 6; i++) {
            int x = 33 + 46 * (i < 3 ? i : i - 3);
            int y = (i < 3 ? 15 : 62);
            fontRenderer.drawString("" + (i + 1), x, y, 0x000000);
        }
        if(receivers.size() > 0) {
            String pageName = "Panel " + receivers.get(currentPage).getIdentifier();
            fontRenderer
                .drawString(pageName, 170 + fontRenderer.getStringWidth(
                    pageName
                ) / 2, 17, 0x404040);
        }
        if (activePlug != -1) {
            int width = this.width / 2;
            int height = this.height / 2;
            int plugX = width + 45 + (20 * (activePlug < 4 ? activePlug : activePlug - 4));
            int plugY = height - (activePlug < 4 ? 65 : 45);
            int xDist = plugX - mouseX;
            int yDist = plugY - mouseY;
            float lineWidth = 2;
            if (Minecraft.getMinecraft().currentScreen != null) {
                long distanceSq = xDist * xDist + yDist * yDist;
                int screenDim = Minecraft.getMinecraft().currentScreen.width * Minecraft.getMinecraft().currentScreen.height;
                float percentOfDim = Math.min(1, distanceSq / (float) screenDim);
                lineWidth = 1 + ((1 - (percentOfDim)) * 3);
            }
            final int color = 0x13C90A;
            int red = (color >> 16) & 255;
            int green =  (color >> 8) & 255;
            int blue =  (color) & 255;
            int alpha = (color >> 24) & 255;
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.glLineWidth(3);
            GlStateManager.color(1F, 1F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(plugX - guiLeft, plugY - guiTop, 0).color(red, green, blue, 255).endVertex();
            bufferBuilder.pos(mouseX - guiLeft, mouseY - guiTop, 0).color(red, green, blue, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.dmxStartField.drawTextBox();
    }


    public void generateButtons() {
        int width = this.width / 2;
        int height = this.height / 2;
        this.buttonList.removeAll(sockets);
        this.buttonList.removeAll(plugs);
        this.sockets.clear();
        this.plugs.clear();
        for (int i = 0; i < 6; i++) {
            SocapexPatch[] patch = inventoryPlayer.getPatch(i);
            int x = (width - 95) + 46 * (i < 3 ? i : i - 3);
            int y = height - (i < 3 ? 75 : 30);
            for (int j = 0; j < 2; j++) {
                if (patch == null || j >= patch.length) {
                    sockets.add(new ButtonSocket(inventoryPlayer, 300 + (i * 10) + j, x, y + 20 * j, tileDimmerRack.getDmxStart() + i));
                } else {
                    SocapexPatch patch1 = patch[j];
                    if (patch1 != null && patch1.getReceiver() != null) {
                        String identifier = inventoryPlayer.getIdentifier(patch1.getReceiver());
                        sockets.add(new ButtonSocket(inventoryPlayer, 300 + (i * 10) + j, x, y + 20 * j, tileDimmerRack.getDmxStart() + i, patch1, identifier));
                    } else {
                        sockets.add(new ButtonSocket(inventoryPlayer, 300 + (i * 10) + j, x, y + 20 * j, tileDimmerRack.getDmxStart() + i));
                    }
                }
            }
        }
        buttonList.addAll(sockets);
        if(receivers.size() > 0) {
            ISocapexReceiver iSocapexReceiver = receivers.get(currentPage);
            int[] channels = inventoryPlayer.getChannelsForReceiver(iSocapexReceiver);
            for (int i = 0; i < channels.length; i++) {
                int x = width + 45 + (20 * (i < 4 ? i : i - 4));
                int y = height - (i < 4 ? 65 : 45);
                if (channels[i] != 1) {
                    plugs.add(new ButtonPlug(200 + i, x, y, i + 1, iSocapexReceiver.getIdentifier(), activePlug == i));
                }
            }
        }
        buttonList.addAll(plugs);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        int width = this.width / 2;
        int height = this.height / 2;
        this.buttonList.add(new GuiButton(101, width + 45, height - 90, 15, 20, "<"));
        this.buttonList.add(new GuiButton(102, width + 43 + 60, height - 90, 15, 20, ">"));
        this.dmxStartField = new GuiTextField(50, this.fontRenderer, width + 43, height - 20, 50, 10);
        this.dmxStartField.setFocused(true);
        if (tileDimmerRack.hasCapability(DMXReceiver.CAP, EnumFacing.SOUTH)) {
            this.dmxStartField.setText(Integer.toString(tileDimmerRack.getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getStartPoint()));
        }
        generateButtons();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dmxStartField.mouseClicked(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.dmxStartField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        TheatricalPacketHandler.INSTANCE.sendToServer(new UpdateDMXStartAddressPacket(Integer.parseInt(this.dmxStartField.getText()), tileDimmerRack.getPos()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        generateButtons();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 101) {
                if (currentPage - 1 < 0) {
                    currentPage = receivers.size() - 1;
                } else {
                    currentPage--;
                }
                activePlug = -1;
                generateButtons();
            } else if (button.id == 102) {
                if (currentPage + 1 > receivers.size() - 1) {
                    currentPage = 0;
                } else {
                    currentPage++;
                }
                activePlug = -1;
                generateButtons();
            } else if (button.id >= 200 && button.id < 300) {
                int plug = button.id - 200;
                if (activePlug == -1) {
                    activePlug = plug;
                    ((ButtonPlug) button).setActive(true);
                } else {
                    if (activePlug == plug) {
                        activePlug = -1;
                        ((ButtonPlug) button).setActive(false);
                    }
                }
            } else if (button.id >= 300) {
                int id = button.id - 300;
                int channel = 0;
                if (id >= 10) {
                    channel = id / 10;
                } else {
                    channel = 0;
                }
                int socket = id >= 10 ? id - (10 * channel) : id;
                ButtonSocket socketButton = (ButtonSocket) button;
                if (activePlug == -1) {
                    if (socketButton.isPatched()) {
                        TheatricalPacketHandler.INSTANCE.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, new SocapexPatch(), socket));
                        generateButtons();
                    }
                } else {
                    if (!socketButton.isPatched()) {
                        SocapexPatch patch = new SocapexPatch(receivers.get(currentPage).getPos(), activePlug);
                        TheatricalPacketHandler.INSTANCE.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, patch, socket));
                        activePlug = -1;
                        generateButtons();
                    }
                }
            }
        }
    }

}
