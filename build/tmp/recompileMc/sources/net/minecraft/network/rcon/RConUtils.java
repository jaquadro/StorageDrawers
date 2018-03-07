package net.minecraft.network.rcon;

import com.google.common.base.Charsets;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class RConUtils
{
    /** Translation array of decimal to hex digits */
    public static final char[] HEX_DIGITS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Read a null-terminated string from the given byte array
     */
    public static String getBytesAsString(byte[] p_72661_0_, int p_72661_1_, int p_72661_2_)
    {
        int i = p_72661_2_ - 1;
        int j;

        for (j = p_72661_1_ > i ? i : p_72661_1_; 0 != p_72661_0_[j] && j < i; ++j)
        {
            ;
        }

        return new String(p_72661_0_, p_72661_1_, j - p_72661_1_, Charsets.UTF_8);
    }

    /**
     * Read 4 bytes from the
     */
    public static int getRemainingBytesAsLEInt(byte[] p_72662_0_, int p_72662_1_)
    {
        return getBytesAsLEInt(p_72662_0_, p_72662_1_, p_72662_0_.length);
    }

    /**
     * Read 4 bytes from the given array in little-endian format and return them as an int
     */
    public static int getBytesAsLEInt(byte[] p_72665_0_, int p_72665_1_, int p_72665_2_)
    {
        return 0 > p_72665_2_ - p_72665_1_ - 4 ? 0 : p_72665_0_[p_72665_1_ + 3] << 24 | (p_72665_0_[p_72665_1_ + 2] & 255) << 16 | (p_72665_0_[p_72665_1_ + 1] & 255) << 8 | p_72665_0_[p_72665_1_] & 255;
    }

    /**
     * Read 4 bytes from the given array in big-endian format and return them as an int
     */
    public static int getBytesAsBEint(byte[] p_72664_0_, int p_72664_1_, int p_72664_2_)
    {
        return 0 > p_72664_2_ - p_72664_1_ - 4 ? 0 : p_72664_0_[p_72664_1_] << 24 | (p_72664_0_[p_72664_1_ + 1] & 255) << 16 | (p_72664_0_[p_72664_1_ + 2] & 255) << 8 | p_72664_0_[p_72664_1_ + 3] & 255;
    }

    /**
     * Returns a String representation of the byte in hexadecimal format
     */
    public static String getByteAsHexString(byte input)
    {
        return "" + HEX_DIGITS[(input & 240) >>> 4] + HEX_DIGITS[input & 15];
    }
}