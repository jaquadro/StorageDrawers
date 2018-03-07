package net.minecraft.client.gui.spectator;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuObject
{
    void selectItem(SpectatorMenu menu);

    ITextComponent getSpectatorName();

    void renderIcon(float p_178663_1_, int alpha);

    boolean isEnabled();
}