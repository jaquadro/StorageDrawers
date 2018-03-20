package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil
{
    /**
     * Handles pumping inbound packets across threads by checking whether the current thread is actually the main thread
     * for the side in question. If not, then the packet is pumped into the handler queue in the main thread so that it
     * can be handled synchronously by the recipient, it then throws an exception to terminate the current packet
     * handler thread.
     */
    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packetIn, final T processor, IThreadListener scheduler) throws ThreadQuickExitException
    {
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    packetIn.processPacket(processor);
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
    }
}