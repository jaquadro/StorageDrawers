package com.jaquadro.minecraft.storagedrawers.client.model.decorator;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.DrawerModelStore;
import com.jaquadro.minecraft.storagedrawers.client.model.context.DrawerModelContext;
import com.jaquadro.minecraft.storagedrawers.client.model.context.FramedModelContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DrawerModelDecorator extends ModelDecorator<DrawerModelContext>
{
    protected final DrawerModelStore.DecorationSet overlays;

    public DrawerModelDecorator (DrawerModelStore.DecorationSet overlays) {
        this.overlays = overlays;
    }

    @Override
    public List<RenderType> getRenderTypes (BlockState state) {
        return List.of(RenderType.cutoutMipped());
    }

    @Override
    public List<RenderType> getRenderTypes (ItemStack stack) {
        return List.of(RenderType.cutoutMipped());
    }

    @Override
    public void emitQuads (Supplier<DrawerModelContext> contextSupplier, Consumer<BakedModel> emitModel) {
        DrawerModelContext context = contextSupplier.get();
        if (context == null)
            return;

        emitDecoratedQuads(context, emitModel);
    }

    public void emitDecoratedQuads(DrawerModelContext context, Consumer<BakedModel> emitModel) {
        Direction dir = context.state().getValue(BlockDrawers.FACING);
        boolean half = false;
        Block block = context.state().getBlock();
        if (block instanceof BlockDrawers drawers)
            half = drawers.isHalfDepth();

        IDrawerAttributes attr = context.attr();
        if (attr == null)
            return;

        IProtectable protectable = context.protectable();

        boolean isLocked = attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED);
        boolean isClaimed = protectable != null && protectable.getOwner() != null;

        if (isLocked && isClaimed)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.LOCK_CLAIM, dir, half));
        else if (isLocked)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.LOCK, dir, half));
        else if (isClaimed)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.CLAIM, dir, half));

        DrawerModelStore.DynamicPart priorityPart = switch (attr.getPriority()) {
            case 1 -> DrawerModelStore.DynamicPart.PRIORITY_P1;
            case 2 -> DrawerModelStore.DynamicPart.PRIORITY_P2;
            case -1 -> DrawerModelStore.DynamicPart.PRIORITY_N1;
            case -2 -> DrawerModelStore.DynamicPart.PRIORITY_N2;
            default -> null;
        };
        if (priorityPart != null)
            emitModel.accept(DrawerModelStore.getModel(priorityPart, dir, half));

        if (attr.isVoid())
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.VOID, dir, half));
        if (attr.isConcealed())
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.SHROUD, dir, half));
        if (attr.hasFillLevel()) {
            if (block instanceof BlockCompDrawers compBlock) {
                int count = compBlock.getDrawerCount();
                emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.INDICATOR_COMP, dir, half, count));
            } else if (block instanceof BlockStandardDrawers stdBlock) {
                int count = stdBlock.getDrawerCount();
                emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.INDICATOR, dir, half, count));
            }
        }
        if (block instanceof BlockStandardDrawers) {
            IDrawerGroup group = context.group();
            if (group != null) {
                int count = group.getDrawerCount();
                DrawerModelStore.DynamicPart[] groupMissingSlots = DrawerModelStore.missingSlots[count - 1];
                for (int i = 0; i < groupMissingSlots.length; i++) {
                    if (group.getDrawer(i).isMissing())
                        emitModel.accept(DrawerModelStore.getModel(groupMissingSlots[i], dir, half, count));
                }
            }
        }
    }
}
