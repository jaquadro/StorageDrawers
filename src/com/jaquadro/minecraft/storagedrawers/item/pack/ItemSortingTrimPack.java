package com.jaquadro.minecraft.storagedrawers.item.pack;

import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingTrimPack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemSortingTrimPack extends ItemTrimPack
{
    public ItemSortingTrimPack (Block block) {
        super(block, getUnlocalizedNames(block));
    }

    private static String[] getUnlocalizedNames (Block block) {
        if (block instanceof BlockSortingTrimPack)
            return ((BlockSortingTrimPack) block).getUnlocalizedNames();
        else
            return new String[16];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(itemStack, player, list, par4);
        list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("storageDrawers.waila.sorting"));
    }
}