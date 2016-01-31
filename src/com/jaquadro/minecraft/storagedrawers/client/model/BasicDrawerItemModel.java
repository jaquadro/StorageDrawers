package com.jaquadro.minecraft.storagedrawers.client.model;

import com.google.common.primitives.Ints;
import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

import java.awt.*;
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
        return baseModel.getFaceQuads(facing);
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        if (!sealed || !(block instanceof BlockDrawers))
            return baseModel.getGeneralQuads();

        List<BakedQuad> combined = new ArrayList<BakedQuad>(baseModel.getGeneralQuads());
        combined.add(createSealedQuad(ModBlocks.basicDrawers, 0));
        return combined;
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

    private BakedQuad createSealedQuad (BlockDrawers block, int itemRenderLayer) {
        IBlockState blockState = block.getStateFromMeta(meta);
        float depth = block.isHalfDepth(blockState) ? .5f : 1f;
        TextureAtlasSprite iconTape = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconTapeCover);

        return new BakedQuad(Ints.concat(
            vertexToInts(0, 0, 1 - depth, Color.WHITE.getRGB(), iconTape, 16, 16),
            vertexToInts(0, 1, 1 - depth, Color.WHITE.getRGB(), iconTape, 16, 0),
            vertexToInts(1, 1, 1 - depth, Color.WHITE.getRGB(), iconTape, 0, 0),
            vertexToInts(1, 0, 1 - depth, Color.WHITE.getRGB(), iconTape, 0, 16)
        ), itemRenderLayer, EnumFacing.NORTH);
    }

    private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v)
    {
        return new int[] {
            Float.floatToRawIntBits(x),
            Float.floatToRawIntBits(y),
            Float.floatToRawIntBits(z),
            color,
            Float.floatToRawIntBits(texture.getInterpolatedU(u)),
            Float.floatToRawIntBits(texture.getInterpolatedV(v)),
            0
        };
    }
}
