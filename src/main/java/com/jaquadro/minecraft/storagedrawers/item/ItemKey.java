/*package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributes;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerAttributesModifiable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemKey extends Item
{
    @CapabilityInject(IDrawerAttributes.class)
    public static Capability<IDrawerAttributes> DRAWER_ATTRIBUTES_CAPABILITY = null;

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (@Nonnull ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag advanced) {
        String name = getUnlocalizedName(itemStack);
        list.add(I18n.format(name + ".description"));
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D () {
        return true;
    }

    @Override
    @Nonnull
    public Multimap<String, AttributeModifier> getAttributeModifiers (EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND)
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)2, 0));

        return multimap;
    }

    @Override
    public EnumActionResult onItemUse (EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null)
            return EnumActionResult.PASS;

        IDrawerAttributes attrs = tile.getCapability(DRAWER_ATTRIBUTES_CAPABILITY, null);
        if (!(attrs instanceof IDrawerAttributesModifiable))
            return EnumActionResult.PASS;

        handleDrawerAttributes((IDrawerAttributesModifiable)attrs);

        return EnumActionResult.SUCCESS;
    }

    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) { }
}
*/