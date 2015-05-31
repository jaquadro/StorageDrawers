package com.jaquadro.minecraft.storagedrawers.core.handlers;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.gui.GuiDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers1;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers2;
import com.jaquadro.minecraft.storagedrawers.inventory.ContainerDrawers4;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    public static int drawersGuiID = 0;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrawers)
            return new ContainerDrawers(player.inventory, (TileEntityDrawers) tile);

        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrawers)
            return new GuiDrawers(player.inventory, (TileEntityDrawers) tile);

        return null;
    }

    private ContainerDrawers getContainer (TileEntityDrawers tile, InventoryPlayer playerInventory) {
        switch (tile.getDrawerCount()) {
            case 1:
                return new ContainerDrawers1(playerInventory, tile);
            case 2:
                return new ContainerDrawers2(playerInventory, tile);
            case 4:
                return new ContainerDrawers4(playerInventory, tile);
            default:
                return null;
        }
    }
}
