package thaumcraft.api.research;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.internal.CommonInternals;

public class ScanOreDictionary implements IScanThing {
	
	String research;	
	String[] entries;	
	
	public ConcurrentHashMap<Integer,Boolean> cache = new ConcurrentHashMap<>();

	public ScanOreDictionary(String research, String ... entries) {
		this.research = research;
		this.entries = entries;
	}
	
	@Override
	public boolean checkThing(EntityPlayer player, Object obj) {	
		ItemStack stack = null;
		if (obj!=null) {
			if (obj instanceof BlockPos) {
				IBlockState state = player.world.getBlockState((BlockPos) obj);
				stack = state.getBlock().getItem(player.world, (BlockPos) obj, state);			
			}
			else
			if (obj instanceof ItemStack) 
				stack = (ItemStack) obj;
			else
			if (obj instanceof EntityItem && ((EntityItem)obj).getItem()!=null) 
				stack = ((EntityItem)obj).getItem();
		}
		
		if (stack!=null && !stack.isEmpty()) {			
			int hid = CommonInternals.generateUniqueItemstackId(stack);
			if (cache.containsKey(hid)) {
				return cache.get(hid);
			}
			
			int[] ids = OreDictionary.getOreIDs(stack);
			for (String entry:entries) {
				for (int id:ids) {
					if (OreDictionary.getOreName(id).equals(entry)) {
						synchronized(cache) { cache.put(hid,true); }
						return true;
					}
				}
			}
			synchronized(cache) { cache.put(hid,false); }
		}
		
		return false;
	}
	
	@Override
	public String getResearchKey(EntityPlayer player, Object object) {		
		return research;
	}
}
