package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.block.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.client.model.BasicDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.CompDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.TrimModel;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import com.jaquadro.minecraft.storagedrawers.item.ItemBasicDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
    @SubscribeEvent
    public void onModelBakeEvent (ModelBakeEvent event) {
        BasicDrawerModel.initialize(event.modelRegistry);
        CompDrawerModel.initialize(event.modelRegistry);
        //TrimModel.initialize(event.modelRegistry);
    }

    @Override
    public void registerRenderers () {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        renderItem.getItemModelMesher().register(ModItems.upgradeTemplate, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.upgradeTemplate), "inventory"));
        renderItem.getItemModelMesher().register(ModItems.upgradeVoid, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.upgradeVoid), "inventory"));
        renderItem.getItemModelMesher().register(ModItems.drawerKey, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.drawerKey), "inventory"));

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
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.controllerSlave), 0, new ModelResourceLocation(ModBlocks.getQualifiedName(ModBlocks.controllerSlave), "inventory"));

        // Basic Drawers

        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                String resName = ModBlocks.getQualifiedName(ModBlocks.basicDrawers) + "_" + type.getName() + "_" + material.getName();
                ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.basicDrawers), resName);
            }
        }

        if (Item.getItemFromBlock(ModBlocks.basicDrawers) instanceof ItemBasicDrawers) {
            ItemBasicDrawers itemDrawers = (ItemBasicDrawers)Item.getItemFromBlock(ModBlocks.basicDrawers);
            renderItem.getItemModelMesher().register(itemDrawers, itemDrawers.getMeshResolver());
        }

        // Comp Drawers

        for (EnumCompDrawer slots : EnumCompDrawer.values()) {
            String resName = ModBlocks.getQualifiedName(ModBlocks.compDrawers) + "_" + slots.getName();
            ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.compDrawers), resName);

            renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.compDrawers), slots.getMetadata(), new ModelResourceLocation(resName, "inventory"));
        }

        // Trim

        for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
            String resName = ModBlocks.getQualifiedName(ModBlocks.trim) + "_" + material.getName();
            ModelBakery.addVariantName(Item.getItemFromBlock(ModBlocks.trim), resName);

            renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.trim), material.getMetadata(), new ModelResourceLocation(resName, "inventory"));
        }
    }
}
