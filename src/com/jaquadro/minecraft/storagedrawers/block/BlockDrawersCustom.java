package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockDrawersCustom extends BlockDrawers
{
    @SideOnly(Side.CLIENT)
    private IIcon overlayHandle;
    @SideOnly(Side.CLIENT)
    private IIcon overlayFaceShadow;
    @SideOnly(Side.CLIENT)
    private IIcon overlayTrimShadow;

    public BlockDrawersCustom (String blockName, int drawerCount, boolean halfDepth) {
        super(blockName, drawerCount, halfDepth);
    }

    @Override
    public int getRenderType () {
        return StorageDrawers.proxy.drawersCustomRenderID;
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
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        if (StorageDrawers.config.cache.addonShowVanilla)
            list.add(new ItemStack(item));
    }

    @SideOnly(Side.CLIENT)
    public IIcon getHandleOverlay () {
        return overlayHandle;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFaceShadowOverlay () {
        return overlayFaceShadow;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getTrimShadowOverlay () {
        return overlayTrimShadow;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        super.registerBlockIcons(register);

        overlayHandle = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/handle_" + drawerCount);
        overlayFaceShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_face_" + drawerCount);
        overlayTrimShadow = register.registerIcon(StorageDrawers.MOD_ID + ":overlay/shading_trim_" + drawerCount);

        iconSide[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconSideV[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconSideH[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconTrim[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_side");
        iconFront1[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_1");
        iconFront2[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_2");
        iconFront4[0] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_raw_front_4");
    }
}
