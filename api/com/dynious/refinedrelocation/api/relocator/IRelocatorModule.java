package com.dynious.refinedrelocation.api.relocator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import java.util.List;

public interface IRelocatorModule
{
    public void init(IItemRelocator relocator, int side);

    public boolean onActivated(IItemRelocator relocator, EntityPlayer player, int side, ItemStack stack);

    public void onUpdate(IItemRelocator relocator, int side);

    public ItemStack outputToSide(IItemRelocator relocator, int side, TileEntity inventory, ItemStack stack, boolean simulate);

    public boolean isItemDestination();

    public ItemStack receiveItemStack(IItemRelocator relocator, int side, ItemStack stack, boolean input, boolean simulate);

    public void onRedstonePowerChange(boolean isPowered);

    public boolean connectsToRedstone();

    public int strongRedstonePower(int side);

    @SideOnly(Side.CLIENT)
    public GuiScreen getGUI(IItemRelocator relocator, int side, EntityPlayer player);

    public Container getContainer(IItemRelocator relocator, int side, EntityPlayer player);

    public boolean passesFilter(IItemRelocator relocator, int side, ItemStack stack, boolean input, boolean simulate);

    public void readFromNBT(IItemRelocator relocator, int side, NBTTagCompound compound);

    public void writeToNBT(IItemRelocator relocator, int side, NBTTagCompound compound);

    public void readClientData(IItemRelocator relocator, int side, NBTTagCompound compound);

    public void writeClientData(IItemRelocator relocator, int side, NBTTagCompound compound);

    public List<ItemStack> getDrops(IItemRelocator relocator, int side);

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IItemRelocator relocator, int side);

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register);

    public String getDisplayName();

    public List<String> getWailaInformation(NBTTagCompound nbtData);
}
