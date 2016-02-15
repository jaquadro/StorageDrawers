package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFramingTable extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    IIcon iconBase;
    @SideOnly(Side.CLIENT)
    IIcon iconTrim;

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

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock () {
        return false;
    }

    @Override
    public int getRenderType () {
        return StorageDrawers.proxy.framingTableRenderID;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconBase () {
        return iconBase;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim () {
        return iconTrim;
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        iconBase = register.registerIcon(StorageDrawers.MOD_ID + ":base/base_oak");
        iconTrim = register.registerIcon(StorageDrawers.MOD_ID + ":base/trim_oak");
    }
}
