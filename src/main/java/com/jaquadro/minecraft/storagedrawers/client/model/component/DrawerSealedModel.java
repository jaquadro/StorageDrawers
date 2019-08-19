/*package com.jaquadro.minecraft.storagedrawers.client.model.component;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.WrappedChamModel;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class DrawerSealedModel extends WrappedChamModel
{
    public static final ResourceLocation iconTapeCover = new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/tape");

    public DrawerSealedModel (IBakedModel model, IBlockState state, boolean mergeLayers) {
        super(model, state, mergeLayers);
    }

    @Override
    protected void renderMippedLayer (ChamRender renderer, IBlockState state, Object... args) {
        if (!(state.getBlock() instanceof BlockDrawers))
            return;

        BlockDrawers block = (BlockDrawers) state.getBlock();
        float depth = block.isHalfDepth(state) ? .5f : 1f;
        TextureAtlasSprite iconTape = Chameleon.instance.iconRegistry.getIcon(iconTapeCover);

        renderer.startBaking(DefaultVertexFormats.ITEM, 0);
        renderer.setRenderBounds(0, 0, .995f - depth, 1, 1, 1);
        renderer.bakeFace(ChamRender.FACE_ZNEG, state, iconTape, false);
        renderer.stopBaking();
    }
}
*/