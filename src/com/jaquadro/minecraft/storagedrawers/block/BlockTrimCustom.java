package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.item.ItemCustomTrim;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockTrimCustom extends BlockTrim implements ITileEntityProvider
{
    @SideOnly(Side.CLIENT)
    private IIcon defaultFace;
    @SideOnly(Side.CLIENT)
    private IIcon defaultTrim;

    public BlockTrimCustom (String name) {
        super(name);
        isBlockContainer = true;
    }

    @Override
    public int getRenderType () {
        return StorageDrawers.proxy.trimCustomRenderID;
    }

    @Override
    public int getRenderBlockPass () {
        return 1;
    }

    @Override
    public boolean canRenderInPass (int pass) {
        ClientProxy.renderPass = pass;
        return true;
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    protected ItemStack getMainDrop (World world, int x, int y, int z, int metadata) {
        TileEntityTrim tile = getTileEntity(world, x, y, z);
        if (tile == null)
            return ItemCustomTrim.makeItemStack(this, 1, null, null);

        return ItemCustomTrim.makeItemStack(this, 1, tile.getMaterialSide(), tile.getMaterialTrim());
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (StorageDrawers.config.cache.addonShowVanilla)
            list.add(new ItemStack(item));
    }

    public TileEntityTrim getTileEntity (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
        return (tile instanceof TileEntityTrim) ? (TileEntityTrim) tile : null;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultFaceIcon () {
        return defaultFace;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getDefaultTrimIcon () {
        return defaultTrim;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        defaultFace = register.registerIcon(StorageDrawers.MOD_ID + ":base/base_default");
        defaultTrim = register.registerIcon(StorageDrawers.MOD_ID + ":base/trim_default");

        iconTrim[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityTrim();
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived (World world, int x, int y, int z, int eventNum, int eventArg) {
        super.onBlockEventReceived(world, x, y, z, eventNum, eventArg);
        TileEntity tile = world.getTileEntity(x, y, z);
        return (tile != null) && tile.receiveClientEvent(eventNum, eventArg);
    }
}
