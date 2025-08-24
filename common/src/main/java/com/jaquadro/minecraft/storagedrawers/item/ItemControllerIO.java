package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemControllerIO extends BlockItem
{
    public ItemControllerIO (Block block, Properties props) {
        super(block, props);
    }

    @Override
    public Component getName (ItemStack $$0) {
        return Component.translatable("block." + ModConstants.MOD_ID + ".controller_io");
    }
}
