package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFramingTable extends BlockContainer
{
    public BlockFramingTable (String blockName) {
        super(Material.wood);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(2.5f);
        setStepSound(soundTypeWood);
        setBlockName(blockName);
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityFramingTable();
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float vx, float vy, float vz) {
        player.openGui(StorageDrawers.instance, GuiHandler.framingGuiID, world, x, y, z);
        return true;
    }
}
