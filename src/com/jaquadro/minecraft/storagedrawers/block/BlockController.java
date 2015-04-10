package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockController extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon iconSideEtched;
    @SideOnly(Side.CLIENT)
    private IIcon iconTrim;

    public BlockController (String blockName) {
        super(Material.rock);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setBlockName(blockName);
        setStepSound(Block.soundTypeStone);
        setBlockBounds(0, 0, 0, 1, 1, 1);
        setTickRandomly(true);
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
        return StorageDrawers.proxy.controllerRenderID;
    }

    @Override
    public int tickRate (World world) {
        return 100;
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        TileEntityController tile = getTileEntitySafe(world, x, y, z);
        if (tile.getDirection() > 1)
            return;

        int quadrant = MathHelper.floor_double((entity.rotationYaw * 4f / 360f) + .5) & 3;
        switch (quadrant) {
            case 0:
                tile.setDirection(2);
                break;
            case 1:
                tile.setDirection(5);
                break;
            case 2:
                tile.setDirection(3);
                break;
            case 3:
                tile.setDirection(4);
                break;
        }

        if (world.isRemote) {
            tile.invalidate();
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public void onPostBlockPlaced (World world, int x, int y, int z, int meta) {
        if (world.isRemote)
            return;

        TileEntityController te = getTileEntity(world, x, y, z);
        if (te == null)
            return;

        te.updateCache();
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityController te = getTileEntitySafe(world, x, y, z);
        if (te.getDirection() != side)
            return false;

        if (!world.isRemote)
            te.interactPutItemsIntoInventory(player);

        return true;
    }

    @Override
    public boolean isSideSolid (IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (side.ordinal() != getTileEntity(world, x, y, z).getDirection())
            return true;

        return false;
    }

    @Override
    public void updateTick (World world, int x, int y, int z, Random rand) {
        if (world.isRemote)
            return;

        TileEntityController te = getTileEntity(world, x, y, z);
        if (te == null)
            return;

        te.updateCache();

        world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        switch (side) {
            case 0:
            case 1:
                return iconSide;
            case 4:
                return iconFront;
            default:
                return iconSideEtched;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityController tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null)
            return iconFront;

        if (side == tile.getDirection())
            return iconFront;

        switch (side) {
            case 0:
            case 1:
                return iconSide;
            default:
                return iconSideEtched;
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim (int meta) {
        return iconTrim;
    }

    @Override
    public TileEntityController createNewTileEntity (World world, int meta) {
        return new TileEntityController();
    }

    public TileEntityController getTileEntity (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
        return (tile instanceof TileEntityController) ? (TileEntityController) tile : null;
    }

    public TileEntityController getTileEntitySafe (World world, int x, int y, int z) {
        TileEntityController tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            tile = createNewTileEntity(world, world.getBlockMetadata(x, y, z));
            world.setTileEntity(x, y, z, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        iconFront = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_controller_front");
        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side");
        iconSideEtched = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side_2");
        iconTrim = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_trim");
    }
}
