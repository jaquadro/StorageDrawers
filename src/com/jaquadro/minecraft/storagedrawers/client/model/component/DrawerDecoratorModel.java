package com.jaquadro.minecraft.storagedrawers.client.model.component;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.geometry.Area2D;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGeometry;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.List;

public class DrawerDecoratorModel implements IBakedModel
{
    private IBakedModel baseModel;
    private IExtendedBlockState blockState;
    private IDrawerGeometry drawer;
    private EnumFacing dir;
    private boolean shrouded;
    private boolean locked;
    private boolean owned;
    private boolean voiding;
    private boolean[] enabled;

    public DrawerDecoratorModel (IBakedModel baseModel, IExtendedBlockState blockState, IDrawerGeometry drawer, EnumFacing dir, TileEntityDrawers tile) {
        this.baseModel = baseModel;
        this.blockState = blockState;
        this.drawer = drawer;
        this.dir = dir;
        this.shrouded = tile.isShrouded();
        this.locked = tile.isLocked(LockAttribute.LOCK_POPULATED);
        this.owned = tile.getOwner() != null;
        this.voiding = tile.isVoid();

        enabled = new boolean[drawer.getDrawerCount()];
        for (int i = 0; i < enabled.length; i++)
            enabled[i] = !tile.getDrawer(i).isEmpty();
    }

    public static boolean shouldHandleState (TileEntityDrawers tile) {
        return tile.isShrouded() || tile.isVoid() || tile.isLocked(LockAttribute.LOCK_POPULATED) || tile.getOwner() != null;
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        return baseModel.getFaceQuads(facing);
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        ChamRender.instance.startBaking(DefaultVertexFormats.BLOCK);
        if (shrouded)
            buildShroudGeometry();
        if (locked || owned)
            buildLockGeometry();
        if (voiding)
            buildVoidGeometry();

        List<BakedQuad> quads = ChamRender.instance.stopBaking();
        quads.addAll(baseModel.getGeneralQuads());

        return quads;
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

    private void buildLockGeometry () {
        double depth = drawer.isHalfDepth() ? .5 : 1;

        TextureAtlasSprite lockIcon;
        if (locked && owned)
            lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconClaimLockResource);
        else if (locked)
            lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconLockResource);
        else if (owned)
            lockIcon = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconClaimResource);
        else
            return;

        ChamRender.instance.setRenderBounds(0.46875, 0.9375, 0, 0.53125, 1, depth + .003);
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
        ChamRender.instance.bakePartialFace(ChamRender.FACE_ZPOS, blockState, lockIcon, 0, 0, 1, 1, 1, 1, 1);
        ChamRender.instance.state.clearRotateTransform();
    }

    private void buildVoidGeometry () {
        double depth = drawer.isHalfDepth() ? .5 : 1;
        TextureAtlasSprite iconVoid = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconVoidResource);

        ChamRender.instance.setRenderBounds(1 - .0625, 0.9375, 0, 1, 1, depth + .003);
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
        ChamRender.instance.bakePartialFace(ChamRender.FACE_ZPOS, blockState, iconVoid, 0, 0, 1, 1, 1, 1, 1);
        ChamRender.instance.state.clearRotateTransform();
    }

    private void buildShroudGeometry () {
        if (!(blockState.getBlock() instanceof BlockDrawers))
            return;

        BlockDrawers block = (BlockDrawers)blockState.getBlock();
        StatusModelData data = block.getStatusInfo(blockState);
        int count = drawer.getDrawerCount();
        double depth = drawer.isHalfDepth() ? .5 : 1;

        double unit = 0.0625;
        double frontDepth = data.getFrontDepth() * unit;

        TextureAtlasSprite iconCover = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconShroudCover);

        for (int i = 0; i < count; i++) {
            if (!enabled[i])
                continue;

            StatusModelData.Slot slot = data.getSlot(i);
            Area2D bounds = slot.getIconArea();

            ChamRender.instance.setRenderBounds(bounds.getX() * unit, bounds.getY() * unit, 0,
                (bounds.getX() + bounds.getWidth()) * unit, (bounds.getY() + bounds.getHeight()) * unit, depth - frontDepth + .003);
            ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, dir.getIndex());
            ChamRender.instance.bakeFace(ChamRender.FACE_ZPOS, blockState, iconCover, 1, 1, 1);
            ChamRender.instance.state.clearRotateTransform();
        }
    }
}