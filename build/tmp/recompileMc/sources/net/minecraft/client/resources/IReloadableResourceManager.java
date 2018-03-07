package net.minecraft.client.resources;

import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IReloadableResourceManager extends IResourceManager
{
    void reloadResources(List<IResourcePack> resourcesPacksList);

    void registerReloadListener(IResourceManagerReloadListener reloadListener);
}