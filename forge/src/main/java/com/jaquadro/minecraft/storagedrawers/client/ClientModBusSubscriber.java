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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            /*
            I'd like to do this registration in bulk as well, but I'm hesitant to put
            client-only code in common classes like ModContainers (memorizing screen types).
            BERs at least all have the same renderer.
            */
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_1.get(), DrawerScreen.Slot1::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_2.get(), DrawerScreen.Slot2::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_4.get(), DrawerScreen.Slot4::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_2.get(), DrawerScreen.Compacting2::new);
            MenuScreens.register(ModContainers.DRAWER_CONTAINER_COMP_3.get(), DrawerScreen.Compacting3::new);
            MenuScreens.register(ModContainers.FRAMING_TABLE.get(), FramingTableScreen::new);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.KEYBUTTON_DRAWER.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.KEYBUTTON_CONCEALMENT.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.KEYBUTTON_QUANTIFY.get(), RenderType.cutoutMipped());
        });
    }

    public static void registerItemModels() {
        ItemModels.ID_MAPPER.put(
            ModConstants.loc("framed_block"), PlatformDecoratedModel.PlatformDecoratedItemModel.Unbaked.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        ModBlockEntities.DRAWER_TYPES.forEach(ro -> event.registerBlockEntityRenderer(ro.get(), BlockEntityDrawersRenderer::new));
        event.registerBlockEntityRenderer(ModBlockEntities.FRAMING_TABLE.get(), BlockEntityFramingRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(KeyringTooltip.class, t -> new ClientKeyringTooltip(t.contents()));
        event.register(DetachedDrawerTooltip.class, t -> new ClientDetachedDrawerTooltip(t.contents()));
    }

    @SubscribeEvent
    public static void registerTextures (TextureStitchEvent event) {
        if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
            StorageDrawers.log.warn("Block objects not set in TextureStitchEvent.  Is your mod environment broken?");
            return;
        }

        DrawerModelGeometry.loadGeometryData();
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.ModifyBakingResult event) {
        if (ModBlocks.OAK_FULL_DRAWERS_1 == null) {
            StorageDrawers.log.warn("Block objects not set in ModelBakeEvent.  Is your mod environment broken?");
            return;
        }

        DrawerModelStore.getModelLocations().forEach(loc -> {
            DrawerModelStore.tryAddModel(loc, event.getResults().blockStateModels().get(loc));
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
        BlockStateModel parentModel = event.getResults().blockStateModels().get(modelResource);
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
            event.getResults().blockStateModels().put(modelResource, model);
        }
    }
}
