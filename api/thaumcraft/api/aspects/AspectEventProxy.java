package thaumcraft.api.aspects;

import java.util.List;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.internal.CommonInternals;

public class AspectEventProxy {
	
	/**
	 * Used to assign apsects to the given item/block. Here is an example of the declaration for cobblestone:<p>
	 * <i>event.registerObjectTag(new ItemStack(Blocks.COBBLESTONE), (new AspectList()).add(Aspect.ENTROPY, 1).add(Aspect.EARTH, 1));</i>
	 * @param item the item passed. Pass OreDictionary.WILDCARD_VALUE if all damage values of this item/block should have the same aspects
	 * @param aspects A ObjectTags object of the associated aspects
	 */
	public void registerObjectTag(ItemStack item, AspectList aspects) {
		if (aspects==null) aspects=new AspectList();
		try {
			CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(item), aspects);
		} catch (Exception e) {}
	}	
	
	/**
	 * Used to assign apsects to the given ore dictionary item. 
	 * @param oreDict the ore dictionary name
	 * @param aspects A ObjectTags object of the associated aspects
	 */
	public void registerObjectTag(String oreDict, AspectList aspects) {
		if (aspects==null) aspects=new AspectList();
		List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
		if (ores!=null && ores.size()>0) {
			for (ItemStack ore:ores) {
				try {					
					ItemStack oc = ore.copy();
					oc.setCount(1);
					registerObjectTag(oc,aspects.copy());
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Used to assign aspects to the given item/block. 
	 * Attempts to automatically generate aspect tags by checking registered recipes.
	 * Here is an example of the declaration for pistons:<p>
	 * <i>event.registerComplexObjectTag(new ItemStack(Blocks.PISTON), (new AspectList()).add(Aspect.MECHANISM, 2).add(Aspect.MOTION, 4));</i>
	 * IMPORTANT - this should only be used if you are not happy with the default aspects the object would be assigned.
	 * @param item, pass OreDictionary.WILDCARD_VALUE to meta if all damage values of this item/block should have the same aspects
	 * @param aspects A ObjectTags object of the associated aspects
	 */
	public void registerComplexObjectTag(ItemStack item, AspectList aspects ) {
		if (!ThaumcraftApi.exists(item)) {			
			AspectList tmp = AspectHelper.generateTags(item);
			if (tmp != null && tmp.size()>0) {
				for(Aspect tag:tmp.getAspects()) {
					aspects.add(tag, tmp.getAmount(tag));
				}
			}
			registerObjectTag(item,aspects);
		} else {
			AspectList tmp = AspectHelper.getObjectAspects(item);
			for(Aspect tag:aspects.getAspects()) {
				tmp.merge(tag, tmp.getAmount(tag));
			}
			registerObjectTag(item,tmp);
		}
	}
	
	
	/**
	 * Used to assign aspects to the given ore dictionary item. 
	 * Attempts to automatically generate aspect tags by checking registered recipes.
	 * IMPORTANT - this should only be used if you are not happy with the default aspects the object would be assigned.
	 * @param oreDict the ore dictionary name
	 * @param aspects A ObjectTags object of the associated aspects
	 */
	public void registerComplexObjectTag(String oreDict, AspectList aspects) {
		if (aspects==null) aspects=new AspectList();
		List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
		if (ores!=null && ores.size()>0) {
			for (ItemStack ore:ores) {
				try {					
					ItemStack oc = ore.copy();
					oc.setCount(1);
					registerComplexObjectTag(oc,aspects.copy());
				} catch (Exception e) {}
			}
		}
	}

}
