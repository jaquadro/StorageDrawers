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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiConsumer;
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
        return List.of(Sheets.cutoutBlockSheet());
    }

    @Override
    public void emitQuads (Supplier<DrawerModelContext> contextSupplier, BiConsumer<BakedModel, RenderType> emitModel) {
        DrawerModelContext context = contextSupplier.get();
        if (context == null)
            return;

        emitDecoratedQuads(context, emitModel);
    }

    public void emitDecoratedQuads(DrawerModelContext context, BiConsumer<BakedModel, RenderType> emitModel) {
        Direction dir = context.state().getValue(BlockDrawers.FACING);
        boolean drawerHalf = false;
        Block block = context.state().getBlock();
        if (block instanceof BlockDrawers drawers)
            drawerHalf = drawers.isHalfDepth();

        boolean half = drawerHalf;

        IDrawerAttributes attr = context.attr();
        if (attr == null)
            return;

        IProtectable protectable = context.protectable();

        boolean isLocked = attr.isItemLocked(LockAttribute.LOCK_EMPTY) || attr.isItemLocked(LockAttribute.LOCK_POPULATED);
        boolean isClaimed = protectable != null && protectable.getOwner() != null;

        if (isLocked && isClaimed)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.LOCK_CLAIM, dir, half), RenderType.cutoutMipped());
        else if (isLocked)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.LOCK, dir, half), RenderType.cutoutMipped());
        else if (isClaimed)
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.CLAIM, dir, half), RenderType.cutoutMipped());

        BiConsumer<DrawerModelStore.DynamicPart, Integer> emitIcon = (part, index) -> {
            emitModel.accept(DrawerModelStore.getReplacementModel(
                    DrawerModelStore.DynamicPart.RIGHT_LABEL, dir, half, index, part),
                RenderType.cutoutMipped());
        };

        int iconIndex = 1;
        int priority = attr.getPriority();;

        if (attr.isVoid())
            emitIcon.accept(DrawerModelStore.DynamicPart.VOID_ICON, iconIndex++);
        if (priority == -2)
            emitIcon.accept(DrawerModelStore.DynamicPart.PRIORITY_N2_ICON, iconIndex++);
        if (priority == -1)
            emitIcon.accept(DrawerModelStore.DynamicPart.PRIORITY_N1_ICON, iconIndex++);
        if (priority == 1)
            emitIcon.accept(DrawerModelStore.DynamicPart.PRIORITY_P1_ICON, iconIndex++);
        if (priority == 2)
            emitIcon.accept(DrawerModelStore.DynamicPart.PRIORITY_P2_ICON, iconIndex++);
        if (attr.isMagnet())
            emitIcon.accept(DrawerModelStore.DynamicPart.MAGNET_ICON, iconIndex++);
        if (attr.isConcealed())
            emitIcon.accept(DrawerModelStore.DynamicPart.SHROUD_ICON, iconIndex++);
        if (attr.isSuspended())
            emitIcon.accept(DrawerModelStore.DynamicPart.SUSPEND_ICON, iconIndex++);

        if (attr.hasFillLevel()) {
            if (block instanceof BlockCompDrawers compBlock) {
                int count = compBlock.getDrawerCount();
                emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.INDICATOR_COMP, dir, half, count), RenderType.cutoutMipped());
            } else if (block instanceof BlockStandardDrawers stdBlock) {
                int count = stdBlock.getDrawerCount();
                emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.INDICATOR, dir, half, count), RenderType.cutoutMipped());
            }
        }
        if (attr.isHopper())
            emitModel.accept(DrawerModelStore.getModel(DrawerModelStore.DynamicPart.HOPPER), RenderType.cutoutMipped());
        if (block instanceof BlockStandardDrawers) {
            IDrawerGroup group = context.group();
            if (group != null) {
                int count = group.getDrawerCount();
                DrawerModelStore.DynamicPart[] groupMissingSlots = DrawerModelStore.missingSlots[count - 1];
                for (int i = 0; i < groupMissingSlots.length; i++) {
                    if (group.getDrawer(i).isMissing())
                        emitModel.accept(DrawerModelStore.getModel(groupMissingSlots[i], dir, half, count), RenderType.cutoutMipped());
                }
            }
        }
    }
}