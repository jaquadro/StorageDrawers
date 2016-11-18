package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class DrawerInventoryHelper
{
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems (World world, BlockPos pos, IDrawerGroup group) {
        for (int i = 0; i < group.getDrawerCount(); i++) {
            IDrawer drawer = group.getDrawerIfEnabled(i);
            if (drawer == null)
                continue;

            while (drawer.getStoredItemCount() > 0) {
                ItemStack stack = drawer.getStoredItemCopy();
                if (stack.func_190916_E() == 0)
                    break;

                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                drawer.setStoredItemCount(0);
            }
        }
    }

    private static void spawnItemStack (World world, double x, double y, double z, ItemStack stack)
    {
        float xOff = RANDOM.nextFloat() * 0.8F + 0.1F;
        float yOff = RANDOM.nextFloat() * 0.8F + 0.1F;
        float zOff = RANDOM.nextFloat() * 0.8F + 0.1F;

        int stackSize = stack.func_190916_E();
        while (stackSize > 0)
        {
            int dropAmt = RANDOM.nextInt(21) + 10;

            if (dropAmt > stackSize)
                dropAmt = stackSize;

            stack.func_190918_g(dropAmt);
            stackSize = stack.func_190916_E();

            EntityItem entityitem = new EntityItem(world, x + (double)xOff, y + (double)yOff, z + (double)zOff, new ItemStack(stack.getItem(), dropAmt, stack.getMetadata()));

            if (stack.hasTagCompound())
                entityitem.getEntityItem().setTagCompound(stack.getTagCompound().copy());

            float velocity = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double)velocity;
            entityitem.motionY = RANDOM.nextGaussian() * (double)velocity + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)velocity;

            world.spawnEntityInWorld(entityitem);
        }
    }
}
