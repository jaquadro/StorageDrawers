package com.jaquadro.minecraft.storagedrawers.item;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ISealable;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTape extends Item
{
    public ItemTape (String registryName, String unlocalizedName) {
        setRegistryName(registryName);
        setTranslationKey(unlocalizedName);
        setMaxStackSize(1);
        setMaxDamage(8);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        String name = getTranslationKey(itemStack);
        list.add(I18n.format(name + ".description"));
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(pos, side, stack))
            return EnumActionResult.FAIL;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IProtectable) {
            if (!SecurityManager.hasOwnership(player.getGameProfile(), (IProtectable)tile))
                return EnumActionResult.FAIL;
        }

        if (tile instanceof ISealable tileseal) {
            if (tileseal.isSealed())
                return EnumActionResult.FAIL;

            tileseal.setIsSealed(true);
            stack.damageItem(1, player);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }
}
