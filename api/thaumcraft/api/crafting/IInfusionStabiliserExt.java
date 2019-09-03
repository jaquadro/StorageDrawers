package thaumcraft.api.crafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IInfusionStabiliserExt extends IInfusionStabiliser {
	
	/**
	 * This returns how much this object stabilizes infusion. As a baseline, both candles and skulls provide 0.1f.
	 * The amount returned is for a symmetrical pair of the objects, not for each object in the pair
	 * The same amount will be subtracted if the pair isn't symmetrical.
	 * @param world
	 * @param pos
	 * @return 
	 */
	public float getStabilizationAmount(World world, BlockPos pos);
	
	/**
	 * Use this method to do an additional check for symmetry if the default checks are passed. 
	 * If true the penalty will not be getStabilizationAmount, but whatever is returned by getSymmetryPenalty. 
	 * @param world
	 * @param pos1 the first block
	 * @param pos2 the second block as determined by symmetry
	 * @return
	 */
	default public boolean hasSymmetryPenalty(World world, BlockPos pos1, BlockPos pos2) { 
		return false; 
	}
	
	default public float getSymmetryPenalty(World world, BlockPos pos) { 
		return 0; 
	}
	
	

}
