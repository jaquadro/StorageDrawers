package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        //if (stack.hasTag() && stack.getTag().contains("material")) {
        //    String key = stack.getTag().getString("material");
        //    tooltip.add(new TranslationTextComponent("storagedrawers.material", I18n.format("storagedrawers.material." + key)));
        //}

        ITextComponent textCapacity = new TranslationTextComponent("tooltip.storagedrawers.drawers.capacity", getCapacityForBlock(stack));
        tooltip.add(new StringTextComponent("").append(textCapacity).mergeStyle(TextFormatting.GRAY));

        if (stack.hasTag() && stack.getTag().contains("tile")) {
            ITextComponent textSealed = new TranslationTextComponent("tooltip.storagedrawers.drawers.sealed");
            tooltip.add(new StringTextComponent("").append(textSealed).mergeStyle(TextFormatting.YELLOW));
        }

        //tooltip.add(getDescription().applyTextStyle(TextFormatting.GRAY));
    }

    @OnlyIn(Dist.CLIENT)
    protected ITextComponent getDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".desc");
    }

    private int getCapacityForBlock (@Nonnull ItemStack itemStack) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block instanceof BlockDrawers) {
            BlockDrawers drawers = (BlockDrawers)block;
            return drawers.getStorageUnits() * CommonConfig.GENERAL.getBaseStackStorage();
        }

        return 0;
    }
}
