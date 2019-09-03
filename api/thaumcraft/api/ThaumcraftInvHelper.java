package thaumcraft.api;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;

public class ThaumcraftInvHelper {

	public static class InvFilter {
		public boolean igDmg;
		public boolean igNBT;
		public boolean useOre;
		public boolean useMod;
		public boolean relaxedNBT = false;
	
		public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
			this.igDmg = ignoreDamage;
			this.igNBT = ignoreNBT;
			this.useOre = useOre;
			this.useMod = useMod;
		}		
		
		public InvFilter setRelaxedNBT() {
			relaxedNBT = true;
			return this;
		}
		
		public final static InvFilter STRICT = new InvFilter(false,false,false,false);
		public final static InvFilter BASEORE = new InvFilter(false,false,true,false);
	}

	public static IItemHandler getItemHandlerAt(World world, BlockPos pos, EnumFacing side) {
		Pair<IItemHandler, Object> dest = VanillaInventoryCodeHooks.getItemHandler(world, pos.getX(), pos.getY(), pos.getZ(), side);
		if (dest!=null && dest.getLeft()!=null) {
			return dest.getLeft();
		} else {
			TileEntity tileentity = world.getTileEntity(pos);
	        if (tileentity != null && tileentity instanceof IInventory) {            	
	        	return wrapInventory ((IInventory) tileentity, side);
	        }
		}
		return null;
	}

	public static IItemHandler wrapInventory(IInventory inventory, EnumFacing side) {
		return inventory instanceof ISidedInventory? new SidedInvWrapper((ISidedInventory) inventory, side) : new InvWrapper((IInventory) inventory);
	}

	/**
		 * Unlike the normal nbt comparison used by itemstacks, this method only checks if all the tags in stackA is present and equal in stackB. Any extra tags in stackB is ignored. 
		 * Some mods love adding their own nbt data to itemstacks which ends up breaking a lot of crafting recipes or similar checks
		 * This version of the check ignores capabilities as this method is primarily used on my side by things that do not have capabilities in any case.
		 * @param prime
		 * @param other
		 * @return
		 */	
		public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
			if (prime.isEmpty() && other.isEmpty())
		    {
		        return true;
		    }
		    else if (!prime.isEmpty() && !other.isEmpty())
		    {
	//	        if (prime.getTagCompound() == null && other.getTagCompound() != null)
	//	        {
	//	            return false;
	//	        }
	//	        else
	//	        {
		            return (prime.getTagCompound() == null || ThaumcraftInvHelper.compareTagsRelaxed(prime.getTagCompound(),other.getTagCompound()));
	//	        }
		    }
		    else
		    {
		        return false;
		    }
		}

	public static boolean compareTagsRelaxed(NBTTagCompound prime, NBTTagCompound other) {
		for (String key : prime.getKeySet()) {			
			if (!other.hasKey(key) || !prime.getTag(key).equals(other.getTag(key))) {
				return false;
			}
		}		
		return true;
	}

	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in)
	{
		if (stack0==null && in!=null) return false;
		if (stack0!=null && in==null) return false;
		if (stack0==null && in==null) return true;
		
		if (in instanceof Object[]) return true;
		
		if (in instanceof String) {
			List<ItemStack> l = OreDictionary.getOres((String) in,false);
			return ThaumcraftInvHelper.containsMatch(false, new ItemStack[]{stack0}, l);
		}
		
		if (in instanceof ItemStack) {
			//nbt
			boolean t1= !stack0.hasTagCompound() || ThaumcraftInvHelper.areItemStackTagsEqualForCrafting(stack0, (ItemStack) in);		
			if (!t1) return false;	
	        return OreDictionary.itemMatches((ItemStack) in, stack0, false);
		}
		
		return false;
	}

	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets)
	{
	    for (ItemStack input : inputs)
	    {
	        for (ItemStack target : targets)
	        {
	            if (OreDictionary.itemMatches(target, input, strict) && ItemStack.areItemStackTagsEqual(target, input))
	            {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	public static boolean areItemsEqual(ItemStack s1,ItemStack s2)
	{
		if (s1.isItemStackDamageable() && s2.isItemStackDamageable())
		{
			return s1.getItem() == s2.getItem();
		} else
			return s1.getItem() == s2.getItem() && s1.getItemDamage() == s2.getItemDamage();
	}

	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem,ItemStack recipeItem)
	{
		if (recipeItem == null || slotItem == null) return false;
		if (recipeItem.getTagCompound()!=null && slotItem.getTagCompound()==null ) return false;
		if (recipeItem.getTagCompound()==null ) return true;
		
		Iterator iterator = recipeItem.getTagCompound().getKeySet().iterator();
	    while (iterator.hasNext())
	    {
	        String s = (String)iterator.next();
	        if (slotItem.getTagCompound().hasKey(s)) {
	        	if (!slotItem.getTagCompound().getTag(s).toString().equals(
	        			recipeItem.getTagCompound().getTag(s).toString())) {
	        		return false;
	        	}
	        } else {
	    		return false;
	        }
	        
	    }
	    return true;
	}

	public static ItemStack insertStackAt(World world, BlockPos pos, EnumFacing side, ItemStack stack, boolean simulate)
	{		
		IItemHandler inventory = getItemHandlerAt(world,pos,side); 		
		if (inventory!=null) {			
			return ItemHandlerHelper.insertItemStacked(inventory, stack, simulate);
		}
		return stack;
	}
	
	public static ItemStack hasRoomFor(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		if (testStack.isEmpty()) {
			return stack.copy();
		}
		testStack.setCount(stack.getCount() - testStack.getCount()); 
		return testStack;
	}

	public static boolean hasRoomForSome(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		return stack.getCount()==0 || testStack.getCount()!=stack.getCount();
	}
	
	public static boolean hasRoomForAll(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
		return insertStackAt(world, pos, side, stack.copy(), true).isEmpty();
	}

}
