package net.minecraft.util.math;

public class Vec4b
{
    private byte type;
    private byte x;
    private byte y;
    private byte rotation;

    public Vec4b(byte typeIn, byte xIn, byte yIn, byte rotationIn)
    {
        this.type = typeIn;
        this.x = xIn;
        this.y = yIn;
        this.rotation = rotationIn;
    }

    public Vec4b(Vec4b vec)
    {
        this.type = vec.type;
        this.x = vec.x;
        this.y = vec.y;
        this.rotation = vec.rotation;
    }

    public byte getType()
    {
        return this.type;
    }

    public byte getX()
    {
        return this.x;
    }

    public byte getY()
    {
        return this.y;
    }

    public byte getRotation()
    {
        return this.rotation;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Vec4b))
        {
            return false;
        }
        else
        {
            Vec4b vec4b = (Vec4b)p_equals_1_;
            return this.type != vec4b.type ? false : (this.rotation != vec4b.rotation ? false : (this.x != vec4b.x ? false : this.y == vec4b.y));
        }
    }

    public int hashCode()
    {
        int i = this.type;
        i = 31 * i + this.x;
        i = 31 * i + this.y;
        i = 31 * i + this.rotation;
        return i;
    }
}