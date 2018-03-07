package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Timer
{
    /** The number of timer ticks per second of real time */
    float ticksPerSecond;
    /** The time reported by the high-resolution clock at the last call of updateTimer(), in seconds */
    private double lastHRTime;
    /** How many full ticks have turned over since the last call to updateTimer(), capped at 10. */
    public int elapsedTicks;
    /**
     * How much time has elapsed since the last tick, in ticks, for use by display rendering routines (range: 0.0 -
     * 1.0).  This field is frozen if the display is paused to eliminate jitter.
     */
    public float renderPartialTicks;
    /**
     * A multiplier to make the timer (and therefore the game) go faster or slower.  0.5 makes the game run at half-
     * speed.
     */
    public float timerSpeed = 1.0F;
    /** How much time has elapsed since the last tick, in ticks (range: 0.0 - 1.0). */
    public float elapsedPartialTicks;
    /** The time reported by the system clock at the last sync, in milliseconds */
    private long lastSyncSysClock;
    /** The time reported by the high-resolution clock at the last sync, in milliseconds */
    private long lastSyncHRClock;
    /** Increase per 1 every tick, reset when reach 1000 */
    private long counter;
    /** A ratio used to sync the high-resolution clock to the system clock, updated once per second */
    private double timeSyncAdjustment = 1.0D;

    public Timer(float tps)
    {
        this.ticksPerSecond = tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    /**
     * Updates all fields of the Timer using the current time
     */
    public void updateTimer()
    {
        long i = Minecraft.getSystemTime();
        long j = i - this.lastSyncSysClock;
        long k = System.nanoTime() / 1000000L;
        double d0 = (double)k / 1000.0D;

        if (j <= 1000L && j >= 0L)
        {
            this.counter += j;

            if (this.counter > 1000L)
            {
                long l = k - this.lastSyncHRClock;
                double d1 = (double)this.counter / (double)l;
                this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * 0.20000000298023224D;
                this.lastSyncHRClock = k;
                this.counter = 0L;
            }

            if (this.counter < 0L)
            {
                this.lastSyncHRClock = k;
            }
        }
        else
        {
            this.lastHRTime = d0;
        }

        this.lastSyncSysClock = i;
        double d2 = (d0 - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = d0;
        d2 = MathHelper.clamp(d2, 0.0D, 1.0D);
        this.elapsedPartialTicks = (float)((double)this.elapsedPartialTicks + d2 * (double)this.timerSpeed * (double)this.ticksPerSecond);
        this.elapsedTicks = (int)this.elapsedPartialTicks;
        this.elapsedPartialTicks -= (float)this.elapsedTicks;

        if (this.elapsedTicks > 10)
        {
            this.elapsedTicks = 10;
        }

        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}