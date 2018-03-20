package net.minecraft.client.renderer.color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IItemColor
{
    int getColorFromItemstack(ItemStack stack, int tintIndex);
}