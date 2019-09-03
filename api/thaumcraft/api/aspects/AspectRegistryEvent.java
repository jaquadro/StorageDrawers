package thaumcraft.api.aspects;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is called when Thaumcraft is ready to accept the registration of aspects associated with items or entities.
 * Subscribe to this event like you would any other forge event. The <b>register</b> object contains the methods you use to register aspects.
 * <p><i>IMPORTANT: Do NOT instantiate this class or AspectEventProxy and use the methods directly - there is no guarantee that they will work like you expect.</i>
 */
public class AspectRegistryEvent extends Event {
	
	/** this should always be set by TC itself - do not assign your own proxy */
	public AspectEventProxy register;

	public AspectRegistryEvent() {
		
	}
		
	
}
