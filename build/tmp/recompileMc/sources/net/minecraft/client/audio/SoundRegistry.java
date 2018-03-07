package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundRegistry extends RegistrySimple<ResourceLocation, SoundEventAccessor>
{
    /** Contains all registered sound */
    private Map<ResourceLocation, SoundEventAccessor> soundRegistry;

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    protected Map<ResourceLocation, SoundEventAccessor> createUnderlyingMap()
    {
        this.soundRegistry = Maps.<ResourceLocation, SoundEventAccessor>newHashMap();
        return this.soundRegistry;
    }

    public void add(SoundEventAccessor accessor)
    {
        this.putObject(accessor.getLocation(), accessor);
    }

    /**
     * Reset the underlying sound map (Called on resource manager reload)
     */
    public void clearMap()
    {
        this.soundRegistry.clear();
    }
}