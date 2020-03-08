package dev.theatricalmod.theatrical.client.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.theatricalmod.theatrical.block.cables.CableBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class CableRenderer extends TileEntityRenderer<CableBlockEntity> {

    public CableRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CableBlockEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStack.push();
        IVertexBuilder iVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.getCutout());

        matrixStack.pop();
    }
}
