package net.minecraft.client.resources.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BaseMetadataSectionSerializer<T extends IMetadataSection> implements IMetadataSectionSerializer<T>
{
}