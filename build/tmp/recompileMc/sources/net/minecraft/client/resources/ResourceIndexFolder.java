package net.minecraft.client.resources;

import java.io.File;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourceIndexFolder extends ResourceIndex
{
    private final File baseDir;

    public ResourceIndexFolder(File folder)
    {
        this.baseDir = folder;
    }

    public File getFile(ResourceLocation location)
    {
        return new File(this.baseDir, location.toString().replace(':', '/'));
    }

    public File getPackMcmeta()
    {
        return new File(this.baseDir, "pack.mcmeta");
    }
}