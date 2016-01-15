package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.world.World;

public class ItemTrim extends ItemMultiTexture
{
    public ItemTrim (Block block) {
        super(block, block, BlockWood.field_150096_a);
    }

    protected ItemTrim (Block block, String[] names) {
        super(block, block, names);
    }

    @Override
    public boolean doesSneakBypassUse (World world, int x, int y, int z, EntityPlayer player) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockDrawers && ((BlockDrawers) block).retrimType() != null)
            return true;

        return false;
    }
}
