package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.ModServices;
import com.jaquadro.minecraft.storagedrawers.api.event.INetworkedLoadedEvent;
import com.jaquadro.minecraft.storagedrawers.api.event.INetworkedUnloadedEvent;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.util.GridIndex3D;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.LongConsumer;

public class ModINetworkedLocations {
    public static final IEventBus EVENT_BUS = BusBuilder.builder().build();

    // While server is technically single threaded, these days there are fewer and fewer guarantees
    // about threading.  As it's totally conceivable that the event bus or other things will not be on the same
    // thread querying the locations, and the locking is very inexpensive, we just do read/write locking.
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock writeLock = lock.writeLock();
    private static final Lock readLock = lock.readLock();

    private static final GridIndex3D INetworkedLocationsGrid = new GridIndex3D();

    public static void init(ChameleonInit.InitContext ignoredContext) {
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("Registering INetworked location events");
        EVENT_BUS.register(ModINetworkedLocations.class);
    }

    // Unloads happen either from block removal or chunk unloading.
    // We don't actually care which because we don't transfer or count items in unloaded drawers
    @SubscribeEvent
    private static void drawerUnloadHandler(INetworkedUnloadedEvent event) {
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("INetworked of kind {} unloaded at position: {}", event.getEntity().getClass().toString(), event.getPos());
        BlockPos pos = event.getPos();
        writeLock.lock();
        try {
            if (!INetworkedLocationsGrid.remove(pos))
                ModServices.log.error("Failed to remove INetworked at position: {}", pos);
            if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
                ModServices.log.info("Removed INetworked at position: {}", pos);
        } finally {
            writeLock.unlock();
        }
    }

    // Loads happen from either placement or chunk loading.
    //
    @SubscribeEvent
    private static void drawerLoadHandler(INetworkedLoadedEvent event) {
        if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
            ModServices.log.info("INetworked of kind {} loaded at position: {}", event.getEntity().getClass().toString(), event.getPos());
        BlockPos pos = event.getPos();
        writeLock.lock();
        try {
            INetworkedLocationsGrid.add(pos);
            ModServices.log.info("Inserted INetworked at position: {}", pos);
        } finally {
            writeLock.unlock();
        }
    }

    public static List<BlockPos> getINetworkedLocationsInRange(BlockPos center, int range) {
        readLock.lock();
        try {
            // We use a sphere to match how the search was done before.
            if (ModCommonConfig.INSTANCE.GENERAL.debugTrace.get())
                ModServices.log.info("Querying for INetworked locations within range {} at position {}", range, center);
            var gridResults = new ArrayList<BlockPos>();
            LongConsumer c = v -> gridResults.add(BlockPos.of(v));
            INetworkedLocationsGrid.querySphere(center, range, c);
            return gridResults;
        } finally {
            readLock.unlock();
        }
    }
}
