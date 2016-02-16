package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
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
        if ((meta & 0x8) == 0)
            return 0;

        int side = meta & 0x7;
        if (side == 2)
            return -1;
        else if (side == 3)
            return 1;
        else
            return 0;
    }

    private int getZOff (int meta) {
        if ((meta & 0x8) == 0)
            return 0;

        int side = meta & 0x7;
        if (side == 4)
            return 1;
        else if (side == 5)
            return -1;
        else
            return 0;
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
