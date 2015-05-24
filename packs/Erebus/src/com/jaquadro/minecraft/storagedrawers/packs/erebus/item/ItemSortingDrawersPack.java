package com.jaquadro.minecraft.storagedrawers.packs.erebus.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.core.RefinedRelocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemSortingDrawersPack extends ItemDrawersPack
{
    public ItemSortingDrawersPack (Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(itemStack, player, list, par4);

        Block block = Block.getBlockFromItem(itemStack.getItem());
        list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("storageDrawers.waila.sorting"));
    }

    @Override
    protected int getCapacityForBlock (Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (block == RefinedRelocation.fullDrawers1)
            count = config.getBlockBaseStorage("fulldrawers1");
        else if (block == RefinedRelocation.fullDrawers2)
            count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == RefinedRelocation.fullDrawers4)
            count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == RefinedRelocation.halfDrawers2)
            count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == RefinedRelocation.halfDrawers4)
            count = config.getBlockBaseStorage("halfdrawers4");

        return count;
    }
}
