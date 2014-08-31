package mcp.mobius.waila.api;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IWailaFMPProvider {
	/* The classical HEAD/BODY/TAIL text getters */
	List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
	List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
}
