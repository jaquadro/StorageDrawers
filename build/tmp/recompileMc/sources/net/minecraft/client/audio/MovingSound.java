package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class MovingSound extends PositionedSound implements ITickableSound
{
    protected boolean donePlaying;

    protected MovingSound(SoundEvent soundIn, SoundCategory categoryIn)
    {
        super(soundIn, categoryIn);
    }

    public boolean isDonePlaying()
    {
        return this.donePlaying;
    }
}