package com.jaquadro.minecraft.storagedrawers.client;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.gui.ClientDetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.client.gui.ClientKeyringTooltip;
import com.jaquadro.minecraft.storagedrawers.client.model.*;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.BlockEntityFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModContainers;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.FramingTableScreen;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.DetachedDrawerTooltip;
import com.jaquadro.minecraft.storagedrawers.inventory.tooltip.KeyringTooltip;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.function.Function;

@EventBusSubscriber(modid = StorageDrawers.MOD_ID, value = Dist.CLIENT)
public class ClientModBusSubscriber
{
    @SubscribeEvent
    public static void clientSetup(RegisterMenuScreensEvent event) {
        event.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
        event.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
        event.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
        event.register(ModContainers.DRAWER_CONTAINER_COMP_2.get(), DrawerScreen.Compacting2::new);
        event.register(ModContainers.DRAWER_CONTAINER_COMP_3.get(), DrawerScreen.Compacting3::new);
        event.register(ModContainers.FRAMING_TABLE.get(), FramingTableScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        ModBlockEntities.DRAWER_TYPES.forEach(ro -> event.registerBlockEntityRenderer(ro.get(), BlockEntityDrawersRenderer::new));
        event.registerBlockEntityRenderer(ModBlockEntities.FRAMING_TABLE.get(), BlockEntityFramingRenderer::new);
    }

    //@OnlyIn(Dist.CLIENT)
    //public static void setup(FMLClientSetupEvent event) {
    //    ModLoadingContext.get().getActiveContainer().registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    //}

    @SubscribeEvent
    public static void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(KeyringTooltip.class, t -> new ClientKeyringTooltip(t.contents()));
        event.register(DetachedDrawerTooltip.class, t -> new ClientDetachedDrawerTooltip(t.contents()));
    }

    @SubscribeEvent
    public static void registerTextures (TextureAtlasStitchedEvent event) {
        if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
            StorageDrawers.log.warn("Block objects not set in TextureStitchEvent.  Is your mod environment broken?");
            return;
        }

        DrawerModelGeometry.loadGeometryData();
    }

    @SubscribeEvent
    static void registerItemModels(RegisterItemModelsEvent event) {
        event.register(ModConstants.loc("framed_block"), PlatformDecoratedModel.PlatformDecoratedItemModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.ModifyBakingResult event) {
        if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
            StorageDrawers.log.warn("Block objects not set in ModelBakeEvent.  Is your mod environment broken?");
            return;
        }

        DrawerModelStore.getModelLocations().forEach(loc -> {
            DrawerModelStore.tryAddModel(loc, event.getBakingResult().blockStateModels().get(loc));
        });

        ModBlocks.getFramedDrawers().forEach(blockDrawers -> replaceBlock(event, blockDrawers, BakedModelProvider::makeFramedStandardDrawerModel));

        replaceBlock(event, ModBlocks.FRAMED_COMPACTING_DRAWERS_2.get(), BakedModelProvider::makeFramedComp2DrawerModel);
        replaceBlock(event, ModBlocks.FRAMED_COMPACTING_HALF_DRAWERS_2.get(), BakedModelProvider::makeFramedComp2DrawerModel);
        replaceBlock(event, ModBlocks.FRAMED_COMPACTING_DRAWERS_3.get(), BakedModelProvider::makeFramedComp3DrawerModel);
        replaceBlock(event, ModBlocks.FRAMED_COMPACTING_HALF_DRAWERS_3.get(), BakedModelProvider::makeFramedComp3DrawerModel);

        replaceBlock(event, ModBlocks.FRAMED_TRIM.get(), BakedModelProvider::makeFramedTrimModel);
        replaceBlock(event, ModBlocks.FRAMED_CONTROLLER.get(), BakedModelProvider::makeFramedControllerModel);
        replaceBlock(event, ModBlocks.FRAMED_CONTROLLER_IO.get(), BakedModelProvider::makeFramedControllerIOModel);

        ModBlocks.getDrawers().forEach(blockDrawers -> replaceBlock(event, blockDrawers, BakedModelProvider::makeStandardDrawerModel));
    }

    public static void replaceBlock(ModelEvent.ModifyBakingResult event, Block block, Function<BlockStateModel, BlockStateModel> replacer) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            replaceBlock(event, state, replacer);
        }
    }

    private static void replaceBlock (ModelEvent.ModifyBakingResult event, BlockState modelResource, Function<BlockStateModel, BlockStateModel> replacer) {
        BlockStateModel parentModel = event.getBakingResult().blockStateModels().get(modelResource);
        if (parentModel == null) {
            StorageDrawers.log.warn("Got back null model from ModelBakeEvent.ModelManager for resource " + modelResource.toString());
            return;
        }// else if (parentModel == missing)
        //    return;

        if (parentModel instanceof ParentModel)
            return;

        if (DrawerModelStore.INSTANCE.isTargetedModel(modelResource)) {
            BlockStateModel model = replacer.apply(parentModel);
            ItemModelStore.models.put(modelResource, model);
            event.getBakingResult().blockStateModels().put(modelResource, model);
        }
    }
}
