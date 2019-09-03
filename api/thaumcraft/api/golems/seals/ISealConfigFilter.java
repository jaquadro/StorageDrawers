package thaumcraft.api.golems.seals;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ISealConfigFilter {
	
	public NonNullList<ItemStack> getInv();
	
	public NonNullList<Integer> getSizes();
	
	public int getFilterSize();
	
	public ItemStack getFilterSlot(int i);
	
	public int getFilterSlotSize(int i);
	
	public void setFilterSlot(int i, ItemStack stack);
	
	public void setFilterSlotSize(int i, int size);
	
	public boolean isBlacklist();
	
	public void setBlacklist(boolean black);
	
	public boolean hasStacksizeLimiters();

	
	
}
