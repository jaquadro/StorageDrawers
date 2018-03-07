package net.minecraft.client.gui.spectator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuRecipient
{
    void onSpectatorMenuClosed(SpectatorMenu p_175257_1_);
}