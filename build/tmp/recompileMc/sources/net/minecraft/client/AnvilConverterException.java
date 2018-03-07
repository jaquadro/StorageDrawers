package net.minecraft.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnvilConverterException extends Exception
{
    public AnvilConverterException(String exceptionMessage)
    {
        super(exceptionMessage);
    }
}