package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.renderer.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{
    public static int renderPass = 0;
    private DrawersItemRenderer itemRenderer = new DrawersItemRenderer();
    private TrimItemRender trimItemRenderer = new TrimItemRender();

    @Override
    public void registerRenderers () {
        drawersRenderID = RenderingRegistry.getNextAvailableRenderId();
        controllerRenderID = RenderingRegistry.getNextAvailableRenderId();
        drawersCustomRenderID = RenderingRegistry.getNextAvailableRenderId();
        framingTableRenderID = RenderingRegistry.getNextAvailableRenderId();
        trimCustomRenderID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(drawersRenderID, new DrawersRenderer());
        RenderingRegistry.registerBlockHandler(controllerRenderID, new ControllerRenderer());
        RenderingRegistry.registerBlockHandler(drawersCustomRenderID, new DrawersCustomRenderer());
        RenderingRegistry.registerBlockHandler(framingTableRenderID, new FramingTableRenderer());
        RenderingRegistry.registerBlockHandler(trimCustomRenderID, new TrimCustomRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFramingTable.class, new TileEntityFramingRenderer());

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.trimCustom), trimItemRenderer);
    }

    @Override
    public void registerDrawer (Block block) {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
    }
}
