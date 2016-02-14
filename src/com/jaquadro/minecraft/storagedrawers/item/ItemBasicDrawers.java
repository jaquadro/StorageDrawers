package com.jaquadro.minecraft.storagedrawers.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBasicDrawers extends ItemDrawers
{
    protected final Block blockTemplate;
    protected final String[] blockNames;

    public ItemBasicDrawers (Block block) {
        this(block, BlockWood.field_150096_a);
    }

    protected ItemBasicDrawers (Block block, String[] names) {
        super(block);
        setHasSubtypes(true);
        blockTemplate = block;
        blockNames = names;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return blockTemplate.getIcon(2, meta);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getItemDamage();

        if (i < 0 || i >= blockNames.length)
            i = 0;

        return super.getUnlocalizedName() + "." + blockNames[i];
    }
}
