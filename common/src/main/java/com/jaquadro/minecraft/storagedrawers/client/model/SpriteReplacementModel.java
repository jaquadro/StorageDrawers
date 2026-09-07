package com.jaquadro.minecraft.storagedrawers.client.model;

import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.render.ChameleonBlockModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteReplacementModel extends ParentModel
{
    private TextureAtlasSprite sprite;
    private ChunkSectionLayer layer;
    private Map<BlockStateModelPart, ChameleonBlockModelPart> cache = new HashMap<>();

    public SpriteReplacementModel (@NotNull BlockStateModel parent, TextureAtlasSprite sprite) {
        super(parent);
        this.sprite = sprite;
    }

    public SpriteReplacementModel (@NotNull BlockStateModel parent, BlockStateModel replacement, ChunkSectionLayer renderLayer) {
        super(parent);
        this.sprite = replacement.particleMaterial().sprite();
        this.layer = renderLayer;
    }

    public SpriteReplacementModel (@NotNull BlockStateModel parent, ItemStack stack, ChunkSectionLayer renderLayer) {
        super(parent);

        if (stack != null && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(block.defaultBlockState());
            sprite = model.particleMaterial().sprite();
        }

        layer = renderLayer;
    }

    public SpriteReplacementModel (@NotNull BlockStateModel parent, ItemStack stack) {
        this(parent, stack, null);
    }

    @Override
    public void collectParts (RandomSource randomSource, List<BlockStateModelPart> list) {
        if (sprite == null) {
            super.collectParts(randomSource, list);
            return;
        }

        List<BlockStateModelPart> parts = new ArrayList<>();
        parent.collectParts(randomSource, parts);

        parts.forEach(part -> {
            if (cache.containsKey(part))
                list.add(cache.get(part));
            else {
                ChameleonBlockModelPart replacement = ChameleonServices.RENDER.createReplacementPart(part, sprite);
                replacement.setRenderType(layer);

                //if (cache.size() < 10)
                //    cache.put(part, replacement);

                list.add(replacement);
            }
        });
    }

    @Override
    public Material.Baked particleMaterial () {
        if (sprite == null)
            return super.particleMaterial();

        return new Material.Baked(sprite, false);
    }
}
