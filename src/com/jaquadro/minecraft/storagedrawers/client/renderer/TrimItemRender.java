package com.jaquadro.minecraft.storagedrawers.client.renderer;

import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonTrimRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class TrimItemRender implements IItemRenderer
{
    private CommonTrimRenderer commonRender = new CommonTrimRenderer();

    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data) {
        RenderBlocks renderer = getRenderer(data);
        if (renderer == null)
            return;

        Block block = Block.getBlockFromItem(item.getItem());
        if (!(block instanceof BlockTrimCustom))
            return;

        renderTrim((BlockTrimCustom) block, item, renderer, type);
    }

    private void renderTrim (BlockTrimCustom block, ItemStack item, RenderBlocks renderer, ItemRenderType renderType) {
        if (renderType == ItemRenderType.INVENTORY)
            GL11.glRotatef(90, 0, 1, 0);
        if (renderType == ItemRenderType.ENTITY)
            GL11.glRotatef(180, 0, 1, 0);

        if (renderType == ItemRenderType.INVENTORY || renderType == ItemRenderType.ENTITY)
            GL11.glTranslatef(-.5f, -.5f, -.5f);

        ItemStack materialSide = null;
        ItemStack materialTrim = null;

        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.hasKey("MatS"))
                materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
            if (tag.hasKey("MatT"))
                materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
        }

        if (materialSide == null)
            materialSide = new ItemStack(block);
        if (materialTrim == null)
            materialTrim = materialSide;

        IIcon trimIcon = Block.getBlockFromItem(materialTrim.getItem()).getIcon(4, materialTrim.getItemDamage());
        IIcon panelIcon = Block.getBlockFromItem(materialSide.getItem()).getIcon(4, materialSide.getItemDamage());

        if (trimIcon == null)
            trimIcon = block.getDefaultTrimIcon();
        if (panelIcon == null)
            panelIcon = block.getDefaultFaceIcon();

        commonRender.render(null, 0, 0, 0, block, panelIcon, trimIcon);

        if (renderType == ItemRenderType.INVENTORY || renderType == ItemRenderType.ENTITY)
            GL11.glTranslatef(.5f, .5f, .5f);
    }

    private RenderBlocks getRenderer (Object[] data) {
        for (Object obj : data) {
            if (obj instanceof RenderBlocks)
                return (RenderBlocks)obj;
        }

        return null;
    }
}
