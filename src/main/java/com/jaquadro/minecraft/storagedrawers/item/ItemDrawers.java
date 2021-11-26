package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDrawers extends BlockItem
{
    public ItemDrawers (Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        //if (stack.hasTag() && stack.getTag().contains("material")) {
        //    String key = stack.getTag().getString("material");
        //    tooltip.add(new TranslationTextComponent("storagedrawers.material", I18n.format("storagedrawers.material." + key)));
        //}

        Component textCapacity = new TranslatableComponent("tooltip.storagedrawers.drawers.capacity", getCapacityForBlock(stack));
        tooltip.add(new TextComponent("").append(textCapacity).withStyle(ChatFormatting.GRAY));

        if (stack.hasTag() && stack.getTag().contains("tile")) {
            Component textSealed = new TranslatableComponent("tooltip.storagedrawers.drawers.sealed");
            tooltip.add(new TextComponent("").append(textSealed).withStyle(ChatFormatting.YELLOW));
        }

        //tooltip.add(getDescription().applyTextStyle(TextFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    protected Component getDescription() {
        return new TranslatableComponent(this.getDescriptionId() + ".desc");
    }

    private int getCapacityForBlock (@Nonnull ItemStack itemStack) {
        Block block = Block.byItem(itemStack.getItem());
        if (block instanceof BlockDrawers) {
            BlockDrawers drawers = (BlockDrawers)block;
            return drawers.getStorageUnits() * CommonConfig.GENERAL.getBaseStackStorage();
        }

        return 0;
    }
}
