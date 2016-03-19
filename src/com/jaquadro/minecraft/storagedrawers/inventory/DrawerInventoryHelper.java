package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class DrawerInventoryHelper
{
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems (World world, BlockPos pos, IDrawerGroup group) {
        for (int i = 0; i < group.getDrawerCount(); i++) {
            if (!group.isDrawerEnabled(i))
                continue;

            IDrawer drawer = group.getDrawer(i);
            while (drawer.getStoredItemCount() > 0) {
                ItemStack stack = drawer.getStoredItemCopy();
                if (stack == null || stack.stackSize == 0)
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

        while (stack.stackSize > 0)
        {
            int dropAmt = RANDOM.nextInt(21) + 10;

            if (dropAmt > stack.stackSize)
            {
                dropAmt = stack.stackSize;
            }

            stack.stackSize -= dropAmt;
            EntityItem entityitem = new EntityItem(world, x + (double)xOff, y + (double)yOff, z + (double)zOff, new ItemStack(stack.getItem(), dropAmt, stack.getMetadata()));

            if (stack.hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }

            float velocity = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double)velocity;
            entityitem.motionY = RANDOM.nextGaussian() * (double)velocity + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)velocity;

            world.spawnEntityInWorld(entityitem);
        }
    }
}
