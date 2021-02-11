package dev.theatricalmod.theatrical.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonPlug;
import dev.theatricalmod.theatrical.client.gui.widgets.ButtonSocket;
import dev.theatricalmod.theatrical.network.ChangeDimmerPatchPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.network.UpdateDMXAddressPacket;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ScreenDimmerRack extends ContainerScreen<ContainerDimmerRack> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TheatricalMod.MOD_ID, "textures/gui/dimmer_rack.png");

    private final ContainerDimmerRack inventoryPlayer;
    private final TileEntityDimmerRack tileDimmerRack;
    private final List<ISocapexReceiver> receivers;
    private TextFieldWidget dmxStartField;
    private final List<ButtonSocket> sockets;
    private final List<ButtonPlug> plugs;
    private int currentPage = 0;

    private int activePlug = -1;

    public ScreenDimmerRack(ContainerDimmerRack container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.inventoryPlayer = container;
        this.tileDimmerRack = container.dimmerRack;
        this.xSize = 250;
        this.ySize = 131;

        sockets = new ArrayList<>();
        plugs = new ArrayList<>();
        receivers = inventoryPlayer.getDevices();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == GLFW.GLFW_KEY_E || p_keyPressed_1_ == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            this.getMinecraft().player.closeScreen();
            return true;
        }
        return this.dmxStartField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || this.dmxStartField.canWrite() || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    @Override
    public void tick() {
        super.tick();
        generateButtons();
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    public void generateButtons() {
        this.buttons.removeAll(sockets);
        this.buttons.removeAll(plugs);
        this.children.removeAll(sockets);
        this.children.removeAll(plugs);
        this.plugs.clear();
        this.sockets.clear();
        int width = this.width / 2;
        int height = (this.height - this.ySize) / 2;
        for (int i = 0; i < 6; i++) {
            SocapexPatch[] patch = inventoryPlayer.getPatch(i);
            int x = (width - 95) + 46 * (i < 3 ? i : i - 3);
            int y = height + (i < 3 ? 25 : 70);
            for (int j = 0; j < 2; j++) {
                ButtonSocket buttonSocket;
                if (patch == null || j >= patch.length) {
                    buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j), i, j == 1, this::handleSocket);
                } else {
                    SocapexPatch patch1 = patch[j];
                    if (patch1 != null && patch1.getReceiver() != null) {
                        String identifier = inventoryPlayer.getIdentifier(patch1.getReceiver()).toUpperCase().substring(0, 1);
                        buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j), i, j == 1, patch1, identifier, this::handleSocket);
                    } else {
                        buttonSocket = new ButtonSocket(inventoryPlayer, x, y + (20 * j),  i, j == 1, this::handleSocket);
                    }
                }
                this.addButton(buttonSocket);
                sockets.add(buttonSocket);
            }
        }
        if (receivers.size() > 0) {
            ISocapexReceiver iSocapexReceiver = receivers.get(currentPage);
            inventoryPlayer.getChannelsForReceiver(iSocapexReceiver).ifPresent(channels -> {
                for (int i = 0; i < channels.length; i++) {
                    int x = width + 45 + (20 * (i < 3 ? i : i - 3));
                    int y = height + (i < 3 ? 45 : 65);
                    if (channels[i] != 1) {
                        int finalI = i;
                        ButtonPlug buttonPlug = new ButtonPlug(x, y, i + 1, "", activePlug == i, (button) -> {
                            if (button instanceof ButtonPlug) {
                                ButtonPlug plug = (ButtonPlug) button;
                                if (activePlug == finalI) {
                                    plug.setActive(false);
                                    activePlug = -1;
                                } else {
                                    plug.setActive(true);
                                    activePlug = finalI;
                                }
                            }
                        });
                        this.addButton(buttonPlug);
                        plugs.add(buttonPlug);
                    }
                }
            });
        }
    }


    @Override
    protected void init() {
        super.init();
        int lvt_1_1_ = (this.width - this.xSize) / 2;
        int lvt_2_1_ = (this.height - this.ySize) / 2;
        this.addButton(new Button(lvt_1_1_ + 172, lvt_2_1_ + 5, 15, 20, new StringTextComponent("<"), (button) -> {
            if (currentPage - 1 < 0) {
                currentPage = receivers.size() - 1;
            } else {
                currentPage--;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.addButton(new Button(lvt_1_1_ + 165 + 60, lvt_2_1_ + 5, 15, 20, new StringTextComponent(">"), (button) -> {
            if (currentPage + 1 > receivers.size() - 1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            activePlug = -1;
            generateButtons();
        }));
        this.dmxStartField = new TextFieldWidget(this.font, lvt_1_1_ + 172, lvt_2_1_ + 100, 50, 10, new StringTextComponent(""));
        if (tileDimmerRack.getCapability(DMXReceiver.CAP, Direction.SOUTH).isPresent()) {
            this.dmxStartField.setText(Integer.toString(tileDimmerRack.getCapability(DMXReceiver.CAP, Direction.SOUTH).orElse(null).getStartPoint()));
        }
        this.dmxStartField.setCanLoseFocus(false);
        this.dmxStartField.changeFocus(true);
        this.dmxStartField.setTextColor(-1);
        this.dmxStartField.setDisabledTextColour(-1);
        this.dmxStartField.setEnableBackgroundDrawing(true);
        this.dmxStartField.setMaxStringLength(35);
        this.dmxStartField.setValidator(s -> {
            if (s.length() == 0) {
                return true;
            }
            try {
                Integer.parseInt(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        this.children.add(this.dmxStartField);
        this.setFocusedDefault(this.dmxStartField);
        generateButtons();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.dmxStartField.isMouseOver(mouseX, mouseY)){
            this.dmxStartField.setFocused2(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int lvt_4_1_ = this.guiLeft;
        int lvt_5_1_ = (this.height - this.ySize) / 2;
        blit(matrixStack, lvt_4_1_, lvt_5_1_, 0, 0, xSize, ySize, 512, 512);
    }

    @Override
    public void render(MatrixStack ms, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(ms);
        super.render(ms, p_230430_2_, p_230430_3_, p_230430_4_);
        RenderSystem.disableBlend();
        this.dmxStartField.render(ms, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderHoveredTooltip(ms,  p_230430_2_, p_230430_3_);
    }


    @Override
    public void onClose() {
        super.onClose();
        TheatricalNetworkHandler.MAIN.sendToServer(new UpdateDMXAddressPacket(this.container.dimmerRack.getPos(), Integer.parseInt(this.dmxStartField.getText())));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        String name = container.dimmerRack.getDisplayName().getString();
        font.drawString(matrixStack, name, 176 / 2 - font.getStringWidth(name) / 2, 6, 0x404040);
        font.drawString(matrixStack, "Plugs", 180 + font.getStringWidth("Plugs") / 2, 6, 0x404040);
        for (int i = 0; i < 6; i++) {
            int x1 = 33 + 46 * (i < 3 ? i : i - 3);
            int y1 = (i < 3 ? 15 : 62);
            font.drawString(matrixStack, "" + (i + 1), x1, y1, 0x000000);
        }
        if (receivers.size() > 0) {
            String pageName = "Panel " + inventoryPlayer.getIdentifier(receivers.get(currentPage).getReceiverPos());
            font
                    .drawString(matrixStack, pageName, 150 + font.getStringWidth(
                            pageName
                    ) / 2, 30, 0x404040);
        }
        if (activePlug != -1) {
            int width = this.width / 2;
            int height = (this.height - this.ySize) / 2;
            int plugX = width + 45 + (20 * (activePlug < 3 ? activePlug : activePlug - 3));
            int plugY = height + (activePlug < 3 ? 45 : 65);
            int xDist = plugX - x;
            int yDist = plugY - y;
            if (Minecraft.getInstance().currentScreen != null) {
                long distanceSq = xDist * xDist + yDist * yDist;
                int screenDim = Minecraft.getInstance().currentScreen.width * Minecraft.getInstance().currentScreen.height;
                float percentOfDim = Math.min(1, distanceSq / (float) screenDim);
            }
            final int color = 0x13C90A;
            int red = (color >> 16) & 255;
            int green = (color >> 8) & 255;
            int blue = (color) & 255;
            RenderSystem.disableTexture();
            RenderSystem.disableCull();
            RenderSystem.lineWidth(3);
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(plugX - guiLeft, plugY - guiTop, 0).color(red, green, blue, 255).endVertex();
            bufferBuilder.pos(x - guiLeft, y - guiTop, 0).color(red, green, blue, 255).endVertex();
            tessellator.draw();
            RenderSystem.enableTexture();
            RenderSystem.enableCull();
        }
    }

    private void handleSocket(Button button) {
        if (button instanceof ButtonSocket) {
            ButtonSocket socket = (ButtonSocket) button;
            int channel = socket.getChannelNumber();
            int socketNumber = socket.isSecondSocket() ? 2 : 1;
            if (activePlug == -1) {
                if (socket.isPatched()) {
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, socketNumber, new SocapexPatch()));
                    generateButtons();
                }
            } else {
                if (!socket.isPatched()) {
                    SocapexPatch patch1 = new SocapexPatch(receivers.get(currentPage).getReceiverPos(), activePlug);
                    TheatricalNetworkHandler.MAIN.sendToServer(new ChangeDimmerPatchPacket(tileDimmerRack.getPos(), channel, socketNumber, patch1));
                    activePlug = -1;
                    generateButtons();
                }
            }
        }
    }
}
