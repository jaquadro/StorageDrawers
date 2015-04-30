package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ControllerRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers () {
        //drawersRenderID = RenderingRegistry.getNextAvailableRenderId();
        //controllerRenderID = RenderingRegistry.getNextAvailableRenderId();

        //RenderingRegistry.registerBlockHandler(drawersRenderID, new DrawersRenderer());
        //RenderingRegistry.registerBlockHandler(controllerRenderID, new ControllerRenderer());

        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        renderItem.getItemModelMesher().register(ModItems.upgradeTemplate, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.upgradeTemplate), "inventory"));

        for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values()) {
            String resName = ModItems.getQualifiedName(ModItems.upgradeStorage) + "_" + upgrade.getName();
            ModelBakery.addVariantName(ModItems.upgradeStorage, resName);
            renderItem.getItemModelMesher().register(ModItems.upgradeStorage, upgrade.getMetadata(), new ModelResourceLocation(resName, "inventory"));
        }

        for (EnumUpgradeStatus upgrade : EnumUpgradeStatus.values()) {
            String resName = ModItems.getQualifiedName(ModItems.upgradeStatus) + "_" + upgrade.getName();
            ModelBakery.addVariantName(ModItems.upgradeStatus, resName);
            renderItem.getItemModelMesher().register(ModItems.upgradeStatus, upgrade.getMetadata(), new ModelResourceLocation(resName, "inventory"));
        }

        renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.controller), 0, new ModelResourceLocation(ModBlocks.getQualifiedName(ModBlocks.controller), "inventory"));
    }
}
