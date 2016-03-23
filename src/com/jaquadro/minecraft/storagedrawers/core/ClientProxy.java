package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.model.BasicDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.CompDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.CustomDrawerModel;
import com.jaquadro.minecraft.storagedrawers.client.model.FramingTableModel;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.item.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;

public class ClientProxy extends CommonProxy
{
    //private DrawersItemRenderer itemRenderer = new DrawersItemRenderer();

    @SubscribeEvent
    public void onModelBakeEvent (ModelBakeEvent event) {
        //BasicDrawerModel.initialize(event.modelRegistry);
        CompDrawerModel.initialize(event.modelRegistry);
    }

    @Override
    public void initDynamic () {
        ModBlocks.basicDrawers.initDynamic();
        ModBlocks.compDrawers.initDynamic();
        ModBlocks.customDrawers.initDynamic();
    }

    @Override
    public void registerRenderers () {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFramingTable.class, new TileEntityFramingRenderer());

        IconRegistry iconRegistry = Chameleon.instance.iconRegistry;
        iconRegistry.registerIcon(iconLockResource);
        iconRegistry.registerIcon(iconClaimResource);
        iconRegistry.registerIcon(iconClaimLockResource);
        iconRegistry.registerIcon(iconVoidResource);
        iconRegistry.registerIcon(iconIndicatorCompOnResource);
        iconRegistry.registerIcon(iconIndicatorCompOffResource);
        iconRegistry.registerIcon(iconShroudCover);
        iconRegistry.registerIcon(iconTapeCover);

        Chameleon.instance.modelRegistry.registerModel(new BasicDrawerModel.Register());
        Chameleon.instance.modelRegistry.registerModel(new FramingTableModel.Register());
        Chameleon.instance.modelRegistry.registerModel(new CustomDrawerModel.Register());

        for (int i = 0; i < 5; i++) {
            if (iconIndicatorOffResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOffResource[i]);
            if (iconIndicatorOnResource[i] != null)
                iconRegistry.registerIcon(iconIndicatorOnResource[i]);
        }

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        renderItem.getItemModelMesher().register(ModItems.upgradeTemplate, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.upgradeTemplate), "inventory"));
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            renderItem.getItemModelMesher().register(ModItems.upgradeVoid, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.upgradeVoid), "inventory"));
        if (StorageDrawers.config.cache.enableTape)
            renderItem.getItemModelMesher().register(ModItems.tape, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.tape), "inventory"));
        if (StorageDrawers.config.cache.enableLockUpgrades)
            renderItem.getItemModelMesher().register(ModItems.drawerKey, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.drawerKey), "inventory"));
        if (StorageDrawers.config.cache.enableShroudUpgrades)
            renderItem.getItemModelMesher().register(ModItems.shroudKey, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.shroudKey), "inventory"));
        if (StorageDrawers.config.cache.enablePersonalUpgrades)
            renderItem.getItemModelMesher().register(ModItems.personalKey, 0, new ModelResourceLocation(ModItems.getQualifiedName(ModItems.personalKey), "inventory"));

        if (StorageDrawers.config.cache.enableStorageUpgrades)
            registerItemVariants(ModItems.upgradeStorage, ModItems.upgradeStorage.getResourceVariants());
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            registerItemVariants(ModItems.upgradeStatus, ModItems.upgradeStatus.getResourceVariants());
        if (StorageDrawers.config.cache.enableCreativeUpgrades)
            registerItemVariants(ModItems.upgradeCreative, ModItems.upgradeCreative.getResourceVariants());

        if (StorageDrawers.config.cache.enableStorageUpgrades) {
            for (EnumUpgradeStorage upgrade : EnumUpgradeStorage.values()) {
                String resName = ModItems.getQualifiedName(ModItems.upgradeStorage) + "_" + upgrade.getName();
                renderItem.getItemModelMesher().register(ModItems.upgradeStorage, upgrade.getMetadata(), new ModelResourceLocation(resName, "inventory"));
            }
        }

        if (StorageDrawers.config.cache.enableIndicatorUpgrades) {
            for (EnumUpgradeStatus upgrade : EnumUpgradeStatus.values()) {
                String resName = ModItems.getQualifiedName(ModItems.upgradeStatus) + "_" + upgrade.getName();
                renderItem.getItemModelMesher().register(ModItems.upgradeStatus, upgrade.getMetadata(), new ModelResourceLocation(resName, "inventory"));
            }
        }

        if (StorageDrawers.config.cache.enableCreativeUpgrades) {
            for (EnumUpgradeCreative upgrade : EnumUpgradeCreative.values()) {
                String resName = ModItems.getQualifiedName(ModItems.upgradeCreative) + "_" + upgrade.getName();
                renderItem.getItemModelMesher().register(ModItems.upgradeCreative, upgrade.getMetadata(), new ModelResourceLocation(resName, "inventory"));
            }
        }

        if (StorageDrawers.config.isBlockEnabled("controller"))
            renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.controller), 0, new ModelResourceLocation(ModBlocks.getQualifiedName(ModBlocks.controller), "inventory"));
        if (StorageDrawers.config.isBlockEnabled("controllerslave"))
            renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.controllerSlave), 0, new ModelResourceLocation(ModBlocks.getQualifiedName(ModBlocks.controllerSlave), "inventory"));

        // Basic Drawers

        registerItemVariants(ModBlocks.basicDrawers, ModBlocks.basicDrawers.getResourceVariants());

        /*if (Item.getItemFromBlock(ModBlocks.basicDrawers) instanceof ItemBasicDrawers) {
            ItemBasicDrawers itemDrawers = (ItemBasicDrawers)Item.getItemFromBlock(ModBlocks.basicDrawers);
            renderItem.getItemModelMesher().register(itemDrawers, itemDrawers.getMeshResolver());
        }*/

        // Comp Drawers

        if (StorageDrawers.config.isBlockEnabled("compdrawers")) {
            registerItemVariants(ModBlocks.compDrawers, ModBlocks.compDrawers.getResourceVariants());

            for (EnumCompDrawer slots : EnumCompDrawer.values()) {
                String resName = ModBlocks.getQualifiedName(ModBlocks.compDrawers) + "_" + slots.getName();
                renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.compDrawers), slots.getMetadata(), new ModelResourceLocation(resName, "inventory"));
            }
        }

        // Trim

        if (StorageDrawers.config.isBlockEnabled("trim")) {
            registerItemVariants(ModBlocks.trim, ModBlocks.trim.getResourceVariants());

            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                String resName = ModBlocks.getQualifiedName(ModBlocks.trim) + "_" + material.getName();
                renderItem.getItemModelMesher().register(Item.getItemFromBlock(ModBlocks.trim), material.getMetadata(), new ModelResourceLocation(resName, "inventory"));
            }
        }
    }

    @Override
    public void registerDrawer (Block block) {
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
    }

    private void registerItemVariants (Block block, String... variants) {
        registerItemVariants(Item.getItemFromBlock(block), variants);
    }

    private void registerItemVariants (Item item, String... variants) {
        ResourceLocation location = GameData.getItemRegistry().getNameForObject(item);
        for (String variant : variants)
            ModelBakery.registerItemVariants(item, new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + variant));
    }
}
