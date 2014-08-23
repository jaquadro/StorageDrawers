package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDrawers extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    @SideOnly(Side.CLIENT)
    private IIcon iconFront2;

    @SideOnly(Side.CLIENT)
    private IIcon iconFront4;

    public BlockDrawers (String blockName) {
        super(Material.wood);

        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(2.5f);
        setStepSound(Block.soundTypeWood);
        setBlockName(blockName);
    }

    @Override
    public void onBlockAdded (World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        setBlockDirection(world, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock () {
        return false;
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public int getRenderType () {
        return ClientProxy.drawersRenderID;
    }

    private void setBlockDirection (World world, int x, int y, int z) {
        if (!world.isRemote) {
            Block blockZNeg = world.getBlock(x, y, z - 1);
            Block blockZPos = world.getBlock(x, y, z + 1);
            Block blockXNeg = world.getBlock(x - 1, y, z);
            Block blockXPos = world.getBlock(x + 1, y, z);

            byte dir = 3;
            if (blockZNeg.func_149730_j() && !blockZPos.func_149730_j())
                dir = 3;
            if (blockZPos.func_149730_j() && !blockZNeg.func_149730_j())
                dir = 2;
            if (blockXNeg.func_149730_j() && !blockXPos.func_149730_j())
                dir = 5;
            if (blockXPos.func_149730_j() && !blockXNeg.func_149730_j())
                dir = 4;

            world.setBlockMetadataWithNotify(x, y, z, dir, 2);
        }
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack item = player.inventory.getCurrentItem();
        if (item == null)
            return false;

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        int slot = (hitY > .5) ? 0 : 1;

        int countAdded = tileDrawers.putItemsIntoSlot(slot, item);

        return countAdded > 0;
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawers();
    }

    public TileEntityDrawers getTileEntity (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, int x, int y, int z) {
        TileEntityDrawers tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            tile = createNewTileEntity(world, world.getBlockMetadata(x, y, z));
            world.setTileEntity(x, y, z, tile);
        }

        return tile;
    }

    @Override
    public IIcon getIcon (int side, int meta) {
        if (side == meta)
            return iconFront2;
        else
            return iconSide;
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        iconFront2 = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_front_2");
        iconFront4 = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_front_4");
        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_side");
    }
}
