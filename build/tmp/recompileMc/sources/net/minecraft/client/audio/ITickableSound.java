package net.minecraft.client.audio;

import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITickableSound extends ISound, ITickable
{
    boolean isDonePlaying();
}