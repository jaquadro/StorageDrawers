package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFramingTable extends BlockContainer
{
    public static final int[][] leftOffset = new int[][] {{0, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    public static final int[][] rightOffset = new int[][] {{0, 0}, {0, 0}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    private Random random = new Random();

    @SideOnly(Side.CLIENT)
    IIcon iconBase;
    @SideOnly(Side.CLIENT)
    IIcon iconTrim;

    @SideOnly(Side.CLIENT)
    IIcon iconOverlayLeft;
    @SideOnly(Side.CLIENT)
    IIcon iconOverlayRight;

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
        int meta = world.getBlockMetadata(x, y, z);
        int priX = x + getXOff(meta);
        int priZ = z + getZOff(meta);
        if (world.getBlock(priX, y, priZ) != this || (world.getBlockMetadata(priX, y, priZ) & 0x8) != 0)
            return false;

        player.openGui(StorageDrawers.instance, GuiHandler.framingGuiID, world, priX, y, priZ);
        return true;
    }

    private int getXOff (int meta) {
        if (isRightBlock(meta))
            return 0;

        return rightOffset[getDirection(meta)][0];
    }

    private int getZOff (int meta) {
        if (isRightBlock(meta))
            return 0;

        return rightOffset[getDirection(meta)][1];
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
    public void onNeighborBlockChange (World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        int side = getDirection(meta);

        if (isRightBlock(meta)) {
            if (world.getBlock(x + leftOffset[side][0], y, z + leftOffset[side][1]) != this) {
                world.setBlockToAir(x, y, z);
                if (!world.isRemote)
                    dropBlockAsItem(world, x, y, z, 1, 0);
            }
        }
        else if (world.getBlock(x + rightOffset[side][0], y, z + rightOffset[side][1]) != this)
            world.setBlockToAir(x, y, z);
    }

    @Override
    public Item getItemDropped (int meta, Random random, int fortune) {
        return isPrimaryBlock(meta) ? Item.getItemFromBlock(ModBlocks.framingTable) : Item.getItemById(0);
    }

    @Override
    public void dropBlockAsItemWithChance (World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (isPrimaryBlock(meta))
            super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
    }

    @Override
    public int getMobilityFlag () {
        return 1;
    }

    @Override
    public void onBlockHarvested (World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && !isPrimaryBlock(meta)) {
            int side = getDirection(meta);
            x += rightOffset[side][0];
            z += rightOffset[side][1];

            if (world.getBlock(x, y, z) == this)
                world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, Block block, int side) {
        TileEntityFramingTable tile = (TileEntityFramingTable)world.getTileEntity(x, y, z);
        if (tile != null && isPrimaryBlock(world.getBlockMetadata(x, y, z))) {
            for (int i = 0, n = tile.getSizeInventory(); i < n; i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack == null)
                    continue;

                float fx = random.nextFloat() * .8f + .1f;
                float fy = random.nextFloat() * .8f + .1f;
                float fz = random.nextFloat() * .8f + .1f;

                while (stack.stackSize > 0) {
                    int amount = random.nextInt(21) + 10;
                    if (amount > stack.stackSize)
                        amount = stack.stackSize;

                    stack.stackSize -= amount;
                    EntityItem entity = new EntityItem(world, x + fx, y + fy, z + fz, new ItemStack(stack.getItem(), amount, stack.getItemDamage()));

                    if (stack.hasTagCompound())
                        entity.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());

                    entity.motionX = random.nextGaussian() * .05f;
                    entity.motionY = random.nextGaussian() * .05f + .2f;
                    entity.motionZ = random.nextGaussian() * .05f;

                    world.spawnEntityInWorld(entity);
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, side);
    }

    public static int getDirection (int meta) {
        return meta & 0x7;
    }

    public static boolean isRightBlock (int meta) {
        return (meta & 0x8) == 0;
    }

    public static boolean isPrimaryBlock (int meta) {
        return isRightBlock(meta);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconBase () {
        return iconBase;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim () {
        return iconTrim;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconOverlay (boolean left) {
        return left ? iconOverlayLeft : iconOverlayRight;
    }

    @Override
    public void registerBlockIcons (IIconRegister register) {
        iconBase = register.registerIcon(StorageDrawers.MOD_ID + ":base/base_oak");
        iconTrim = register.registerIcon(StorageDrawers.MOD_ID + ":base/trim_oak");

        iconOverlayLeft = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_worktable_left");
        iconOverlayRight = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_worktable_right");
    }
}
