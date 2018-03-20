package net.minecraft.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DimensionType
{
    OVERWORLD(0, "Overworld", "", WorldProviderSurface.class),
    NETHER(-1, "Nether", "_nether", WorldProviderHell.class),
    THE_END(1, "The End", "_end", WorldProviderEnd.class);

    private final int id;
    private final String name;
    private final String suffix;
    private final Class <? extends WorldProvider > clazz;
    private boolean shouldLoadSpawn = false;

    private DimensionType(int idIn, String nameIn, String suffixIn, Class <? extends WorldProvider > clazzIn)
    {
        this.id = idIn;
        this.name = nameIn;
        this.suffix = suffixIn;
        this.clazz = clazzIn;
        this.shouldLoadSpawn = idIn == 0;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public WorldProvider createDimension()
    {
        try
        {
            Constructor <? extends WorldProvider > constructor = this.clazz.getConstructor(new Class[0]);
            return (WorldProvider)constructor.newInstance(new Object[0]);
        }
        catch (NoSuchMethodException nosuchmethodexception)
        {
            throw new Error("Could not create new dimension", nosuchmethodexception);
        }
        catch (InvocationTargetException invocationtargetexception)
        {
            throw new Error("Could not create new dimension", invocationtargetexception);
        }
        catch (InstantiationException instantiationexception)
        {
            throw new Error("Could not create new dimension", instantiationexception);
        }
        catch (IllegalAccessException illegalaccessexception)
        {
            throw new Error("Could not create new dimension", illegalaccessexception);
        }
    }

    public static DimensionType getById(int id)
    {
        for (DimensionType dimensiontype : values())
        {
            if (dimensiontype.getId() == id)
            {
                return dimensiontype;
            }
        }

        throw new IllegalArgumentException("Invalid dimension id " + id);
    }

    public boolean shouldLoadSpawn(){ return this.shouldLoadSpawn; }
    public DimensionType setLoadSpawn(boolean value) { this.shouldLoadSpawn = value; return this; }

    private static Class<?>[] ENUM_ARGS = {int.class, String.class, String.class, Class.class};
    static { net.minecraftforge.common.util.EnumHelper.testEnum(DimensionType.class, ENUM_ARGS); }
    public static DimensionType register(String name, String suffix, int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        String enum_name = name.replace(" ", "_").toLowerCase();
        DimensionType ret = net.minecraftforge.common.util.EnumHelper.addEnum(DimensionType.class, enum_name, ENUM_ARGS,
                id, name, suffix, provider);
        return ret.setLoadSpawn(keepLoaded);
    }
    //TODO: Unregister? There is no way to really delete a enum value...
}