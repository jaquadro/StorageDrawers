package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ObjectHolder(StorageDrawers.MOD_ID)
public class ModBlocks
{
    public static final BlockDrawers
        OAK_FULL_DRAWERS_1 = null,
        OAK_FULL_DRAWERS_2 = null,
        OAK_FULL_DRAWERS_4 = null,
        OAK_HALF_DRAWERS_1 = null,
        OAK_HALF_DRAWERS_2 = null,
        OAK_HALF_DRAWERS_4 = null,
        SPRUCE_FULL_DRAWERS_1 = null,
        SPRUCE_FULL_DRAWERS_2 = null,
        SPRUCE_FULL_DRAWERS_4 = null,
        SPRUCE_HALF_DRAWERS_1 = null,
        SPRUCE_HALF_DRAWERS_2 = null,
        SPRUCE_HALF_DRAWERS_4 = null,
        BIRCH_FULL_DRAWERS_1 = null,
        BIRCH_FULL_DRAWERS_2 = null,
        BIRCH_FULL_DRAWERS_4 = null,
        BIRCH_HALF_DRAWERS_1 = null,
        BIRCH_HALF_DRAWERS_2 = null,
        BIRCH_HALF_DRAWERS_4 = null,
        JUNGLE_FULL_DRAWERS_1 = null,
        JUNGLE_FULL_DRAWERS_2 = null,
        JUNGLE_FULL_DRAWERS_4 = null,
        JUNGLE_HALF_DRAWERS_1 = null,
        JUNGLE_HALF_DRAWERS_2 = null,
        JUNGLE_HALF_DRAWERS_4 = null,
        ACACIA_FULL_DRAWERS_1 = null,
        ACACIA_FULL_DRAWERS_2 = null,
        ACACIA_FULL_DRAWERS_4 = null,
        ACACIA_HALF_DRAWERS_1 = null,
        ACACIA_HALF_DRAWERS_2 = null,
        ACACIA_HALF_DRAWERS_4 = null,
        DARK_OAK_FULL_DRAWERS_1 = null,
        DARK_OAK_FULL_DRAWERS_2 = null,
        DARK_OAK_FULL_DRAWERS_4 = null,
        DARK_OAK_HALF_DRAWERS_1 = null,
        DARK_OAK_HALF_DRAWERS_2 = null,
        DARK_OAK_HALF_DRAWERS_4 = null;

    public static final TileEntityType<?>
        STANDARD_DRAWERS_1 = null,
        STANDARD_DRAWERS_2 = null,
        STANDARD_DRAWERS_4 = null;
    
    /*@ObjectHolder(StorageDrawers.MOD_ID + ":basicdrawers")
    public static BlockDrawers basicDrawers;
    @ObjectHolder(StorageDrawers.MOD_ID + ":compdrawers")
    public static BlockCompDrawers compDrawers;
    @ObjectHolder(StorageDrawers.MOD_ID + ":controller")
    public static BlockController controller;
    @ObjectHolder(StorageDrawers.MOD_ID + ":controllerslave")
    public static BlockSlave controllerSlave;
    @ObjectHolder(StorageDrawers.MOD_ID + ":trim")
    public static BlockTrim trim;
    @ObjectHolder(StorageDrawers.MOD_ID + ":framingtable")
    public static BlockFramingTable framingTable;
    @ObjectHolder(StorageDrawers.MOD_ID + ":customdrawers")
    public static BlockDrawersCustom customDrawers;
    @ObjectHolder(StorageDrawers.MOD_ID + ":customtrim")
    public static BlockTrimCustom customTrim;
    @ObjectHolder(StorageDrawers.MOD_ID + ":keybutton")
    public static BlockKeyButton keyButton;*/

    @Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration
    {
        private static List<Block> blockList = new ArrayList<Block>();

        @SubscribeEvent
        public static void registerBlocks (RegistryEvent.Register<Block> event) {
            registerDrawerBlock(event, "oak_full_drawers_1", 1, false);
            registerDrawerBlock(event, "oak_full_drawers_2", 2, false);
            registerDrawerBlock(event, "oak_full_drawers_4", 4, false);
            registerDrawerBlock(event, "oak_half_drawers_1", 1, true);
            registerDrawerBlock(event, "oak_half_drawers_2", 2, true);
            registerDrawerBlock(event, "oak_half_drawers_4", 4, true);
            registerDrawerBlock(event, "spruce_full_drawers_1", 1, false);
            registerDrawerBlock(event, "spruce_full_drawers_2", 2, false);
            registerDrawerBlock(event, "spruce_full_drawers_4", 4, false);
            registerDrawerBlock(event, "spruce_half_drawers_1", 1, true);
            registerDrawerBlock(event, "spruce_half_drawers_2", 2, true);
            registerDrawerBlock(event, "spruce_half_drawers_4", 4, true);
            registerDrawerBlock(event, "birch_full_drawers_1", 1, false);
            registerDrawerBlock(event, "birch_full_drawers_2", 2, false);
            registerDrawerBlock(event, "birch_full_drawers_4", 4, false);
            registerDrawerBlock(event, "birch_half_drawers_1", 1, true);
            registerDrawerBlock(event, "birch_half_drawers_2", 2, true);
            registerDrawerBlock(event, "birch_half_drawers_4", 4, true);
            registerDrawerBlock(event, "jungle_full_drawers_1", 1, false);
            registerDrawerBlock(event, "jungle_full_drawers_2", 2, false);
            registerDrawerBlock(event, "jungle_full_drawers_4", 4, false);
            registerDrawerBlock(event, "jungle_half_drawers_1", 1, true);
            registerDrawerBlock(event, "jungle_half_drawers_2", 2, true);
            registerDrawerBlock(event, "jungle_half_drawers_4", 4, true);
            registerDrawerBlock(event, "acacia_full_drawers_1", 1, false);
            registerDrawerBlock(event, "acacia_full_drawers_2", 2, false);
            registerDrawerBlock(event, "acacia_full_drawers_4", 4, false);
            registerDrawerBlock(event, "acacia_half_drawers_1", 1, true);
            registerDrawerBlock(event, "acacia_half_drawers_2", 2, true);
            registerDrawerBlock(event, "acacia_half_drawers_4", 4, true);
            registerDrawerBlock(event, "dark_oak_full_drawers_1", 1, false);
            registerDrawerBlock(event, "dark_oak_full_drawers_2", 2, false);
            registerDrawerBlock(event, "dark_oak_full_drawers_4", 4, false);
            registerDrawerBlock(event, "dark_oak_half_drawers_1", 1, true);
            registerDrawerBlock(event, "dark_oak_half_drawers_2", 2, true);
            registerDrawerBlock(event, "dark_oak_half_drawers_4", 4, true);

            /*IForgeRegistry<Block> registry = event.getRegistry();
            ConfigManager config = StorageDrawers.config;

            registry.registerAll(
                new BlockVariantDrawers("basicdrawers", StorageDrawers.MOD_ID + ".basicDrawers"),
                new BlockKeyButton("keybutton", StorageDrawers.MOD_ID + ".keyButton")
            );

            GameRegistry.registerTileEntity(TileEntityDrawersStandard.Legacy.class, StorageDrawers.MOD_ID + ":basicdrawers");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.Slot1.class, StorageDrawers.MOD_ID + ":basicdrawers.1");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.Slot2.class, StorageDrawers.MOD_ID + ":basicdrawers.2");
            GameRegistry.registerTileEntity(TileEntityDrawersStandard.Slot4.class, StorageDrawers.MOD_ID + ":basicdrawers.4");

            GameRegistry.registerTileEntity(TileEntityKeyButton.class, StorageDrawers.MOD_ID + ":keybutton");

            if (config.isBlockEnabled("compdrawers")) {
                registry.register(new BlockCompDrawers("compdrawers", StorageDrawers.MOD_ID + ".compDrawers"));
                GameRegistry.registerTileEntity(TileEntityDrawersComp.class, StorageDrawers.MOD_ID + ":compdrawers");
            }
            if (config.isBlockEnabled("controller")) {
                registry.register(new BlockController("controller", StorageDrawers.MOD_ID + ".controller"));
                GameRegistry.registerTileEntity(TileEntityController.class, StorageDrawers.MOD_ID + ":controller");
            }
            if (config.isBlockEnabled("controllerSlave")) {
                registry.register(new BlockSlave("controllerslave", StorageDrawers.MOD_ID + ".controllerSlave"));
                GameRegistry.registerTileEntity(TileEntitySlave.class, StorageDrawers.MOD_ID + ":controllerslave");
            }
            if (config.isBlockEnabled("trim")) {
                registry.register(new BlockTrim("trim", StorageDrawers.MOD_ID + ".trim"));
            }

            if (config.cache.enableFramedDrawers) {
                registry.registerAll(
                    new BlockFramingTable("framingtable", StorageDrawers.MOD_ID + ".framingTable"),
                    new BlockDrawersCustom("customdrawers", StorageDrawers.MOD_ID + ".customDrawers"),
                    new BlockTrimCustom("customtrim", StorageDrawers.MOD_ID + ".customTrim")
                );

                GameRegistry.registerTileEntity(TileEntityFramingTable.class, StorageDrawers.MOD_ID + ":framingtable");
                GameRegistry.registerTileEntity(TileEntityTrim.class, StorageDrawers.MOD_ID + ":trim");
            }*/
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
            registerTileEntity(event, "standard_drawers_1", TileEntityDrawersStandard.Slot1::new,
                OAK_FULL_DRAWERS_1,
                OAK_HALF_DRAWERS_1,
                SPRUCE_FULL_DRAWERS_1,
                SPRUCE_HALF_DRAWERS_1,
                BIRCH_FULL_DRAWERS_1,
                BIRCH_HALF_DRAWERS_1,
                JUNGLE_FULL_DRAWERS_1,
                JUNGLE_HALF_DRAWERS_1,
                ACACIA_FULL_DRAWERS_1,
                ACACIA_HALF_DRAWERS_1,
                DARK_OAK_FULL_DRAWERS_1,
                DARK_OAK_HALF_DRAWERS_1);
            
            registerTileEntity(event, "standard_drawers_2", TileEntityDrawersStandard.Slot1::new,
                OAK_FULL_DRAWERS_2,
                OAK_HALF_DRAWERS_2,
                SPRUCE_FULL_DRAWERS_2,
                SPRUCE_HALF_DRAWERS_2,
                BIRCH_FULL_DRAWERS_2,
                BIRCH_HALF_DRAWERS_2,
                JUNGLE_FULL_DRAWERS_2,
                JUNGLE_HALF_DRAWERS_2,
                ACACIA_FULL_DRAWERS_2,
                ACACIA_HALF_DRAWERS_2,
                DARK_OAK_FULL_DRAWERS_2,
                DARK_OAK_HALF_DRAWERS_2);

            registerTileEntity(event, "standard_drawers_4", TileEntityDrawersStandard.Slot1::new,
                OAK_FULL_DRAWERS_4,
                OAK_HALF_DRAWERS_4,
                SPRUCE_FULL_DRAWERS_4,
                SPRUCE_HALF_DRAWERS_4,
                BIRCH_FULL_DRAWERS_4,
                BIRCH_HALF_DRAWERS_4,
                JUNGLE_FULL_DRAWERS_4,
                JUNGLE_HALF_DRAWERS_4,
                ACACIA_FULL_DRAWERS_4,
                ACACIA_HALF_DRAWERS_4,
                DARK_OAK_FULL_DRAWERS_4,
                DARK_OAK_HALF_DRAWERS_4);
        }

        private static Block registerDrawerBlock(RegistryEvent.Register<Block> event, String name, int drawerCount, boolean halfDepth) {
            return registerBlock(event, name, new BlockStandardDrawers(drawerCount, halfDepth, Block.Properties.create(Material.WOOD)
                .sound(SoundType.WOOD).hardnessAndResistance(5f)));
        }

        private static Block registerBlock(RegistryEvent.Register<Block> event, String name, Block block) {
            return registerBlock(event, name, block, blockList);
        }

        private static Block registerBlock(RegistryEvent.Register<Block> event, String name, Block block, List<Block> group) {
            block.setRegistryName(name);
            event.getRegistry().register(block);
            group.add(block);

            return block;
        }

        private static <T extends TileEntity> void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event, String name, Supplier<? extends T> factory, Block... blocks) {
            event.getRegistry().register(TileEntityType.Builder.create(factory, blocks)
                .build(null).setRegistryName(new ResourceLocation(StorageDrawers.MOD_ID, name)));
        }

        @SubscribeEvent
        public static void registerItems (RegistryEvent.Register<Item> event) {
            for (Block block : blockList) {
                BlockItem itemBlock = new BlockItem(block, new Item.Properties().group(ModItemGroup.STORAGE_DRAWERS));
                itemBlock.setRegistryName(block.getRegistryName());
                event.getRegistry().register(itemBlock);
            }

            /*IForgeRegistry<Item> registry = event.getRegistry();
            ConfigManager config = StorageDrawers.config;

            registry.registerAll(
                new ItemBasicDrawers(basicDrawers).setRegistryName(basicDrawers.getRegistryName()),
                new ItemKeyButton(keyButton).setRegistryName(keyButton.getRegistryName())
            );

            if (config.isBlockEnabled("compdrawers"))
                registry.register(new ItemCompDrawers(compDrawers).setRegistryName(compDrawers.getRegistryName()));
            if (config.isBlockEnabled("controller"))
                registry.register(new ItemController(controller).setRegistryName(controller.getRegistryName()));
            if (config.isBlockEnabled("controllerSlave"))
                registry.register(new ItemBlock(controllerSlave).setRegistryName(controllerSlave.getRegistryName()));
            if (config.isBlockEnabled("trim"))
                registry.register(new ItemTrim(trim).setRegistryName(trim.getRegistryName()));

            if (config.cache.enableFramedDrawers) {
                registry.registerAll(
                    new ItemFramingTable(framingTable).setRegistryName(framingTable.getRegistryName()),
                    new ItemCustomDrawers(customDrawers).setRegistryName(customDrawers.getRegistryName()),
                    new ItemCustomTrim(customTrim).setRegistryName(customTrim.getRegistryName())
                );
            }

            for (String key : new String[] { "drawerBasic" })
                OreDictionary.registerOre(key, new ItemStack(basicDrawers, 1, OreDictionary.WILDCARD_VALUE));
            for (String key : new String[] { "drawerTrim" })
                OreDictionary.registerOre(key, new ItemStack(trim, 1, OreDictionary.WILDCARD_VALUE));*/
        }

        /*
        @SubscribeEvent
        public static void registerRecipes (RegistryEvent.Register<IRecipe> event) {
            IForgeRegistry<IRecipe> registry = event.getRegistry();
            ConfigManager config = StorageDrawers.config;

            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                ItemStack pl = new ItemStack(Blocks.PLANKS, 1, material.getMetadata());
                ItemStack sl = new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata());

                if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
                    ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xxx", " y ", "xxx", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL1.getUnlocalizedName() + "_" + material.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
                    ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL2.getUnlocalizedName() + "_" + material.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
                    ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL4.getUnlocalizedName() + "_" + material.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
                    ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata()), 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF2.getUnlocalizedName() + "_" + material.toString()));
                }
                if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
                    ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata()), 'y', "chestWood")
                        .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF4.getUnlocalizedName() + "_" + material.toString()));
                }
                if (config.isBlockEnabled("trim")) {
                    ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), material.getMetadata());
                    registry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "yyy", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()))
                        .setRegistryName(result.getItem().getRegistryName() + "_" + material.toString()));
                }
            }
        }*/

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerModels (ModelBakeEvent event) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.Slot1.class, new TileEntityDrawersRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.Slot2.class, new TileEntityDrawersRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.Slot4.class, new TileEntityDrawersRenderer());
        }

        //@SubscribeEvent
        //@SideOnly(Side.CLIENT)
        //public static void registerModels (ModelBakeEvent event) {
            //event.getModelRegistry().
            /*if (basicDrawers != null)
                basicDrawers.initDynamic();
            if (compDrawers != null)
                compDrawers.initDynamic();
            if (customDrawers != null)
                customDrawers.initDynamic();

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFramingTable.class, new TileEntityFramingRenderer());

            ModelRegistry modelRegistry = Chameleon.instance.modelRegistry;

            if (basicDrawers != null)
                modelRegistry.registerModel(new BasicDrawerModel.Register());
            if (compDrawers != null)
                modelRegistry.registerModel(new CompDrawerModel.Register());
            if (customDrawers != null) {
                modelRegistry.registerModel(new FramingTableModel.Register());
                modelRegistry.registerModel(new CustomDrawerModel.Register());
                modelRegistry.registerModel(new CustomTrimModel.Register());
            }

            modelRegistry.registerItemVariants(trim);
            modelRegistry.registerItemVariants(controller);
            modelRegistry.registerItemVariants(controllerSlave);
            modelRegistry.registerItemVariants(keyButton);*/
        //}
    }
}
