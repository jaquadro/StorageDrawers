package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

@Environment(EnvType.CLIENT)
public class ModelLoadPlugin implements ModelLoadingPlugin
{
    @Override
    public void onInitializeModelLoader (Context pluginContext) {
        DrawerModelGeometry.loadGeometryData();
        pluginContext.modifyModelAfterBake().register((original, context) -> {
            if (context.topLevelId() == null)
                return original;

            if (context.topLevelId().id().getNamespace().equals(ModConstants.MOD_ID)) {
                DrawerModelStore.tryAddModel(context.topLevelId(), original);

                if (DrawerModelStore.INSTANCE.isTargetedModel(context.topLevelId()))
                    return new PlatformDecoratedDrawerModel(original, DrawerModelStore.INSTANCE);
            }

            return original;
        });
    }
}
