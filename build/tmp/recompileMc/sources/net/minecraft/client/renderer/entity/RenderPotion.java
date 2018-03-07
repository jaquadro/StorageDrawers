package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPotion extends RenderSnowball<EntityPotion>
{
    public RenderPotion(RenderManager renderManagerIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn, Items.POTIONITEM, itemRendererIn);
    }

    public ItemStack getStackToRender(EntityPotion entityIn)
    {
        return entityIn.getPotion();
    }
}