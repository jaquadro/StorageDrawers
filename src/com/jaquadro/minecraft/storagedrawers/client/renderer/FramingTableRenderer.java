package com.jaquadro.minecraft.storagedrawers.client.renderer;

/*
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class FramingTableRenderer implements ISimpleBlockRenderingHandler
{
    private CommonFramingRenderer framingRenderer = new CommonFramingRenderer();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockFramingTable))
            return;

        BlockFramingTable framingTable = (BlockFramingTable)block;

        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        if (blendEnabled)
            GL11.glDisable(GL11.GL_BLEND);

        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        if (!depthMask)
            GL11.glDepthMask(true);

        GL11.glPushMatrix();
        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(.15f, -.5f, -.5f);
        GL11.glScalef(.65f, .65f, .65f);

        RenderHelper.instance.state.setUVRotation(RenderHelper.YPOS, RenderHelper.instance.state.rotateTransform);

        framingRenderer.renderRight(null, 0, 0, 0, framingTable);
        framingRenderer.renderLeft(null, -1, 0, 0, framingTable);

        RenderHelper.instance.state.clearUVRotation(RenderHelper.YPOS);

        if (!blendEnabled)
            GL11.glDisable(GL11.GL_BLEND);
        if (!depthMask)
            GL11.glDepthMask(false);

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockFramingTable))
            return false;

        BlockFramingTable framingTable = (BlockFramingTable)block;
        int meta = world.getBlockMetadata(x, y, z);
        int side = meta & 0x7;
        boolean right = (meta & 0x08) == 0;

        if (side == 2 || side == 3)
            right = !right;

        RenderHelper.instance.state.setRotateTransform(side, RenderHelper.ZNEG);
        RenderHelper.instance.state.setUVRotation(RenderHelper.YPOS, RenderHelper.instance.state.rotateTransform);

        if (ClientProxy.renderPass == 0) {
            if (right)
                framingRenderer.renderRight(world, x, y, z, framingTable);
            else
                framingRenderer.renderLeft(world, x, y, z, framingTable);
        }
        else if (ClientProxy.renderPass == 1) {
            if (right)
                framingRenderer.renderOverlayRight(world, x, y, z, framingTable);
            else
                framingRenderer.renderOverlayLeft(world, x, y, z, framingTable);
        }

        RenderHelper.instance.state.clearRotateTransform();
        RenderHelper.instance.state.clearUVRotation(RenderHelper.YPOS);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId) {
        return true;
    }

    @Override
    public int getRenderId () {
        return StorageDrawers.proxy.framingTableRenderID;
    }
}
*/