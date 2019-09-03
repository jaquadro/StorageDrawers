package thaumcraft.api.crafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * @author Azanor
 * 
 * Blocks that implement this interface act as infusion crafting stabilisers like candles and skulls 
 * 
 * @Deprecated
 * This interface will eventually be combined with IInfusionStabiliserExt
 * Currently they are separate to preserve compatibility with addon mods. 
 *
 */
@Deprecated
public interface IInfusionStabiliser {
	
	/**
	 * returns true if the block can stabilise things
	 */
	public boolean canStabaliseInfusion(World world, BlockPos pos);

}
