package net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IResourceManager
{
    Set<String> getResourceDomains();

    IResource getResource(ResourceLocation location) throws IOException;

    List<IResource> getAllResources(ResourceLocation location) throws IOException;
}