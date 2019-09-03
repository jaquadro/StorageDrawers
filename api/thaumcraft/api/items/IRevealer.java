package thaumcraft.api.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author Azanor
 * 
 * Equipped or held items that extend this class will make nodes or related objects visible in world.
 * 
 * @Deprecated
 * Currently nodes do not exist ingame and in future versions this might be removed and IGoggles (or a capability) will take it's place.
 *
 */

@Deprecated
public interface IRevealer {
	
	/*
	 * If this method returns true the nodes will be visible.
	 */
	public boolean showNodes(ItemStack itemstack, EntityLivingBase player);
	

}
