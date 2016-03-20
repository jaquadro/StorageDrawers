package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.ArrayList;
import java.util.List;

public class BasicDrawerItemModel implements ISmartItemModel
{
    private IBakedModel baseModel;

    private Block block;
    private int meta;
    private boolean sealed;

    public BasicDrawerItemModel (IBakedModel model) {
        baseModel = model;
    }

    @Override
    public IBakedModel handleItemState (ItemStack stack) {
        sealed = false;
        block = null;
        meta = 0;

        if (stack != null) {
            meta = stack.getMetadata();
            block = Block.getBlockFromItem(stack.getItem());
            sealed = stack.hasTagCompound() && stack.getTagCompound().hasKey("tile");
        }

        return this;
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        if (!sealed || !(block instanceof BlockDrawers))
            return baseModel.getFaceQuads(facing);

        List<BakedQuad> combined = new ArrayList<BakedQuad>(baseModel.getFaceQuads(facing));
        combined.addAll(createSealedQuad(ModBlocks.basicDrawers));
        return combined;
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
            return baseModel.getGeneralQuads();
    }

    @Override
    public boolean isAmbientOcclusion () {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d () {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer () {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return baseModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return baseModel.getItemCameraTransforms();
    }

    private List<BakedQuad> createSealedQuad (BlockDrawers block) {
        IBlockState blockState = block.getStateFromMeta(0);
        float depth = ModBlocks.basicDrawers.isHalfDepth(blockState) ? .5f : 1f;
        TextureAtlasSprite iconTape = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconTapeCover);

        ChamRender.instance.startBaking(DefaultVertexFormats.ITEM, 0);
        ChamRender.instance.setRenderBounds(0, 0, .995f - depth, 1, 1, 1);
        ChamRender.instance.bakeFace(ChamRender.FACE_ZNEG, blockState, iconTape, false);
        ChamRender.instance.stopBaking();
        return ChamRender.instance.takeBakedQuads(null);
    }
}
