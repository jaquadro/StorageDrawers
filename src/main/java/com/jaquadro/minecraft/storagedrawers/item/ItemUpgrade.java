package com.jaquadro.minecraft.storagedrawers.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends Item
{
    private boolean allowMultiple;

    public ItemUpgrade (Item.Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag advanced) {
        super.addInformation(itemStack, world, list, advanced);
        list.add(getDescription());
    }

    @OnlyIn(Dist.CLIENT)
    protected ITextComponent getDescription() {
        return new TranslationTextComponent(this.getTranslationKey() + ".desc");
    }

    public void setAllowMultiple (boolean allow) {
        allowMultiple = allow;
    }

    public boolean getAllowMultiple () {
        return allowMultiple;
    }
}
