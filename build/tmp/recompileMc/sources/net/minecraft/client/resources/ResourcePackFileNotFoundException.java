package net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourcePackFileNotFoundException extends FileNotFoundException
{
    public ResourcePackFileNotFoundException(File resourcePack, String p_i1294_2_)
    {
        super(String.format("\'%s\' in ResourcePack \'%s\'", new Object[] {p_i1294_2_, resourcePack}));
    }
}