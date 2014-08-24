package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockDrawers extends BlockContainer implements IExtendedBlockClickHandler
{
    public final boolean halfDepth;
    public final int drawerCount;

    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    @SideOnly(Side.CLIENT)
    private IIcon iconFront2;

    @SideOnly(Side.CLIENT)
    private IIcon iconFront4;

    public BlockDrawers (String blockName, int drawerCount, boolean halfDepth) {
        super(Material.wood);

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;

        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(2.5f);
        setStepSound(Block.soundTypeWood);
        setBlockName(blockName);
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

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntityDrawers tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
            return;
        }

        float depth = halfDepth ? .5f : 1;
        switch (tile.getDirection()) {
            case 2:
                setBlockBounds(0, 0, 1 - depth, 1, 1, 1);
                break;
            case 3:
                setBlockBounds(0, 0, 0, 1, 1, depth);
                break;
            case 4:
                setBlockBounds(1 - depth, 0, 0, 1, 1, 1);
                break;
            case 5:
                setBlockBounds(0, 0, 0, depth, 1, 1);
                break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender () {
        if (halfDepth)
            setBlockBounds(0, 0, 0, 1, 1, .5f);
        else
            setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        TileEntityDrawers tile = getTileEntitySafe(world, x, y, z);
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
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack item = player.inventory.getCurrentItem();
        if (item == null)
            return false;

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        if (tileDrawers.getDirection() != side)
            return false;

        int slot = getDrawerSlot(side, hitX, hitY, hitZ);
        int countAdded = tileDrawers.putItemsIntoSlot(slot, item, 1);

        //if (countAdded > 0)
        //    world.markBlockForUpdate(x, y, z);

        return countAdded > 0;
    }

    private int getDrawerSlot (int side, float hitX, float hitY, float hitZ) {
        if (drawerCount == 2)
            return hitTop(hitY) ? 0 : 1;

        if (hitLeft(side, hitX, hitZ))
            return hitTop(hitY) ? 0 : 1;
        else
            return hitTop(hitY) ? 2 : 3;
    }

    private boolean hitTop (float hitY) {
        return hitY > .5;
    }

    private boolean hitLeft (int side, float hitX, float hitZ) {
        switch (side) {
            case 2:
                return hitX > .5;
            case 3:
                return hitX < .5;
            case 4:
                return hitZ > .5;
            case 5:
                return hitZ < .5;
            default:
                return true;
        }
    }

    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;
            float hitX = (float)(posn.hitVec.xCoord - posn.blockX);
            float hitY = (float)(posn.hitVec.yCoord - posn.blockY);
            float hitZ = (float)(posn.hitVec.zCoord - posn.blockZ);

            StorageDrawers.network.sendToServer(new BlockClickMessage(x, y, z, posn.sideHit, hitX, hitY, hitZ));
        }
    }

    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        if (tileDrawers.getDirection() != side)
            return;

        int slot = (hitY > .5) ? 0 : 1;

        ItemStack item = tileDrawers.takeItemsFromSlot(slot, 1);
        if (item != null && item.stackSize > 0) {
            dropItemStack(world, x, y, z, player, item);
            world.markBlockForUpdate(x, y, z);
        }
    }

    private void dropItemStack (World world, int x, int y, int z, EntityPlayer player, ItemStack stack) {
        EntityItem entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
        world.spawnEntityInWorld(entity);
    }

    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z) {
        if (world.isRemote && player.capabilities.isCreativeMode && !player.isSneaking()) {
            TileEntityDrawers tile = getTileEntity(world, x, y, z);
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;

            if (tile.getDirection() == posn.sideHit) {
                onBlockClicked(world, x, y, z, player);
                return false;
            }
        }

        return super.removedByPlayer(world, player, x, y, z);
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
        return drawerCount == 2 ? iconFront2 : iconFront4;
    }

    @Override
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityDrawers tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null || side == tile.getDirection())
            return drawerCount == 2 ? iconFront2 : iconFront4;

        return iconSide;
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        iconFront2 = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_front_2");
        iconFront4 = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_front_4");
        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_side");
    }
}
