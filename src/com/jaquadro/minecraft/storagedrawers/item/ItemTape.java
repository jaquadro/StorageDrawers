package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ISealable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemTape extends Item
{
    public ItemTape (String name) {
        setUnlocalizedName(name);
        setMaxStackSize(1);
        setMaxDamage(8);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(StatCollector.translateToLocalFormatted(name + ".description"));
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.canPlayerEdit(pos, side, stack))
            return false;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IProtectable) {
            IProtectable protectable = (IProtectable)tile;
            if (protectable.getOwner() != null && !protectable.getOwner().equals(player.getPersistentID()))
                return false;
        }

        if (tile instanceof ISealable) {
            ISealable tileseal = (ISealable) tile;
            if (tileseal.isSealed())
                return false;

            tileseal.setIsSealed(true);
            stack.damageItem(1, player);

            return true;
        }

        return false;
    }
}
