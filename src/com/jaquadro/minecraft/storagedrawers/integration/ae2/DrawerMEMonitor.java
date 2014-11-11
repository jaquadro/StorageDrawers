package com.jaquadro.minecraft.storagedrawers.integration.ae2;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DrawerMEMonitor implements IMEMonitor<IAEItemStack>
{
    private final MEAdaptor adaptor;

    //private final IMEInventoryHandler<StackType> internalHandler;
    private final IItemList<IAEItemStack> cachedList;
    private final HashMap<IMEMonitorHandlerReceiver<IAEItemStack>, Object> listeners = new HashMap<IMEMonitorHandlerReceiver<IAEItemStack>, Object>();

    protected boolean hasChanged = true;

    /*protected IMEInventoryHandler<StackType> getHandler()
    {
        return internalHandler;
    }*/

    protected Iterator<Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> getListeners()
    {
        return listeners.entrySet().iterator();
    }

    protected void postChangesToListeners( Iterable<IAEItemStack> changes, BaseActionSource src)
    {
        notifyListenersOfChange( changes, src );
    }

    protected void notifyListenersOfChange(Iterable<IAEItemStack> diff, BaseActionSource src)
    {
        hasChanged = true;// need to update the cache.
        Iterator<Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object>> i = getListeners();
        while (i.hasNext())
        {
            Map.Entry<IMEMonitorHandlerReceiver<IAEItemStack>, Object> o = i.next();
            IMEMonitorHandlerReceiver<IAEItemStack> recv = o.getKey();
            if ( recv.isValid( o.getValue() ) )
                recv.postChange( this, diff, src );
            else
                i.remove();
        }
    }

    private IAEItemStack monitorDiffrence(IAEStack original, IAEItemStack leftOvers, boolean extraction, BaseActionSource src)
    {
        IAEItemStack diff = (IAEItemStack) original.copy();

        if ( extraction )
            diff.setStackSize( leftOvers == null ? 0 : -leftOvers.getStackSize() );
        else if ( leftOvers != null )
            diff.decStackSize( leftOvers.getStackSize() );

        if ( diff.getStackSize() != 0 )
            postChangesToListeners( ImmutableList.of(diff), src );

        return leftOvers;
    }

    public DrawerMEMonitor (MEAdaptor adaptor) {
        this.adaptor = adaptor;
        cachedList = StorageChannel.ITEMS.createList();
    }

    /*public DrawerMonitorHandler(IMEInventoryHandler<StackType> t) {
        internalHandler = t;
        cachedList = (IItemList<StackType>) t.getChannel().createList();
    }

    public DrawerMonitorHandler(IMEInventoryHandler<StackType> t, StorageChannel chan) {
        internalHandler = t;
        cachedList = (IItemList<StackType>) chan.createList();
    }*/

    @Override
    public void addListener(IMEMonitorHandlerReceiver<IAEItemStack> l, Object verificationToken)
    {
        listeners.put( l, verificationToken );
    }

    @Override
    public void removeListener(IMEMonitorHandlerReceiver<IAEItemStack> l)
    {
        listeners.remove( l );
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable mode, BaseActionSource src)
    {
        if (mode == Actionable.SIMULATE)
            return adaptor.simulateAdd(input);

        return monitorDiffrence(input.copy(), adaptor.addItems(input), false, src);
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src)
    {
        if ( mode == Actionable.SIMULATE )
            return adaptor.simulateRemove(request);

        return monitorDiffrence(request.copy(), adaptor.removeItems(request), true, src);
    }

    @Override
    public IItemList<IAEItemStack> getStorageList()
    {
        if ( hasChanged )
        {
            hasChanged = false;
            cachedList.resetStatus();
            return getAvailableItems( cachedList );
        }

        return cachedList;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList out)
    {
        for (IAEItemStack item : adaptor.getList())
            out.add(item);

        return out;
    }

    @Override
    public StorageChannel getChannel()
    {
        return StorageChannel.ITEMS;
    }

    @Override
    public AccessRestriction getAccess()
    {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEItemStack input)
    {
        return false;
    }

    @Override
    public boolean canAccept(IAEItemStack input)
    {
        return true;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public int getSlot()
    {
        return 0;
    }

    @Override
    public boolean validForPass(int i)
    {
        return true;
    }
}
