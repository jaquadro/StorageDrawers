package thaumcraft.api.crafting;

@Deprecated
/**
 * This is no longer a thing and will be removed in the next major release.
 */
public interface IStabilizable {	
	
	@Deprecated
	public void addStability();
	
	@Deprecated
	public EnumStability getStability();
	
	@Deprecated
	public static enum EnumStability {
		VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE 
	}
}
