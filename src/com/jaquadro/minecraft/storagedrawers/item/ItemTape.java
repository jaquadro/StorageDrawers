package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ISealable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTape extends Item
{
    public ItemTape (String registryName, String unlocalizedName) {
        setRegistryName(registryName);
        setUnlocalizedName(unlocalizedName);
        setMaxStackSize(1);
        setMaxDamage(8);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.translateToLocal(name + ".description"));
    }

    @Override
    public EnumActionResult onItemUse (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.canPlayerEdit(pos, side, stack))
            return EnumActionResult.FAIL;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IProtectable) {
            if (!SecurityManager.hasOwnership(player.getGameProfile(), (IProtectable)tile))
                return EnumActionResult.FAIL;
        }

        if (tile instanceof ISealable) {
            ISealable tileseal = (ISealable) tile;
            if (tileseal.isSealed())
                return EnumActionResult.FAIL;

            tileseal.setIsSealed(true);
            stack.damageItem(1, player);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }
}
