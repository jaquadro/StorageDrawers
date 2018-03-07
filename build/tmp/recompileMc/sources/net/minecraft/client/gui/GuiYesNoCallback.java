package net.minecraft.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface GuiYesNoCallback
{
    void confirmClicked(boolean result, int id);
}