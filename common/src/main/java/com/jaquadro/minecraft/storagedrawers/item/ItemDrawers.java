package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemDrawers extends BlockItem
{
    public ItemDrawers (Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText (ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        //if (stack.hasTag() && stack.getTag().contains("material")) {
        //    String key = stack.getTag().getString("material");
        //    tooltip.add(new TranslationTextComponent("storagedrawers.material", I18n.format("storagedrawers.material." + key)));
        //}

        Component textCapacity = Component.translatable("tooltip.storagedrawers.drawers.capacity", getCapacityForBlock(stack));
        tooltip.add(Component.literal("").append(textCapacity).withStyle(ChatFormatting.GRAY));

        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null) {
            Component textSealed = Component.translatable("tooltip.storagedrawers.drawers.sealed");
            tooltip.add(Component.literal("").append(textSealed).withStyle(ChatFormatting.YELLOW));
        }

        //tooltip.add(getDescription().applyTextStyle(TextFormatting.GRAY));
    }

    @Override
    public Component getName (ItemStack stack) {
        String fallback = null;
        Block block = Block.byItem(stack.getItem());

        if (block instanceof BlockStandardDrawers drawers) {
            String matKey = drawers.getMatKey();
            if (matKey != null) {
                String mat = Component.translatable(drawers.getNameMatKey()).getString();
                fallback = Component.translatable(drawers.getNameTypeKey(), mat).getString();
            }
        }

        return Component.translatableWithFallback(this.getDescriptionId(stack), fallback);
    }

    @NotNull
    public Component getDescription() {
        return Component.translatable(this.getDescriptionId() + ".desc");
    }

    private int getCapacityForBlock (@NotNull ItemStack itemStack) {
        Block block = Block.byItem(itemStack.getItem());
        if (block instanceof BlockDrawers blockDrawers) {
            return blockDrawers.getStorageUnits() * ModCommonConfig.INSTANCE.GENERAL.getBaseStackStorage();
        }

        return 0;
    }
}
