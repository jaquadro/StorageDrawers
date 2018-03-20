package com.jaquadro.minecraft.storagedrawers.core.handlers;

import java.util.Iterator;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.util.LogWriter;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	
	@SubscribeEvent
	public void onInteractDrawerR(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getEntityPlayer().getEntityWorld();
		if (world.getTileEntity(event.getPos()) instanceof TileEntityDrawersStandard)
		{
			TileEntityDrawersStandard drawer = (TileEntityDrawersStandard) world.getTileEntity(event.getPos());
			for (int i = 0 ; drawer.getDrawerCount() > i; i++)
			{
				if (drawer.getDrawer(i).getStoredItemCount() > 1 || drawer.getDrawer(i).getStoredItemCopy() != null)
				{
					if (drawer.getDrawer(i).getStoredItemCopy() != null)
					{
						if (StorageDrawers.itemTracer.containsKey(drawer.getDrawer(i).getStoredItemCopy().getItem()))
						{
							if (StorageDrawers.itemTracer.get(drawer.getDrawer(i).getStoredItemCopy().getItem()) <= drawer.getDrawer(i).getStoredItemCount())
							{
								event.setCanceled(true);
								if (!StorageDrawers.blocklock.contains(event.getPos()))
								{
								LogWriter.warn("[" + event.getEntityPlayer().getName() + "]" + "["+ event.getPos().toString() + "] drawer lock at this position");
								StorageDrawers.blocklock.add(event.getPos());
								}
								return;
							}
						}
					}
				LogWriter.info("[" + event.getEntityPlayer().getName() + "][R]"
						+ "["+ event.getPos().toString() + "]"
								+ "[count :" + drawer.getDrawer(i).getStoredItemCount() + "]"
										+ "[" + ((drawer.getDrawer(i).getStoredItemCopy() != null) ?
												((drawer.getDrawer(i).getStoredItemCopy().hasDisplayName() != true)  ? 
												drawer.getDrawer(i).getStoredItemCopy().getDisplayName() : "null") : "<Empty>")   + "]");
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onInteractDrawerL(PlayerInteractEvent.LeftClickBlock event)
	{
		World world = event.getEntityPlayer().getEntityWorld();
		if (world.getTileEntity(event.getPos()) instanceof TileEntityDrawersStandard)
		{
			TileEntityDrawersStandard drawer = (TileEntityDrawersStandard) world.getTileEntity(event.getPos());
			for (int i = 0 ; drawer.getDrawerCount() > i; i++)
			{
				if (drawer.getDrawer(i).getStoredItemCount() > 1 || drawer.getDrawer(i).getStoredItemCopy() != null)
				{
					if (drawer.getDrawer(i).getStoredItemCopy() != null)
					{
						if (StorageDrawers.itemTracer.containsKey(drawer.getDrawer(i).getStoredItemCopy().getItem()))
						{
							if (StorageDrawers.itemTracer.get(drawer.getDrawer(i).getStoredItemCopy().getItem()) <= drawer.getDrawer(i).getStoredItemCount())
							{
								event.setCanceled(true);
								if (!StorageDrawers.blocklock.contains(event.getPos()))
								{
								LogWriter.warn("[" + event.getEntityPlayer().getName() + "]" + "["+ event.getPos().toString() + "] drawer lock at this position");
								
								StorageDrawers.blocklock.add(event.getPos());
								}
								return;
							}
						}
					}
				LogWriter.info("[" + event.getEntityPlayer().getName() + "][L]"
						+ "["+ event.getPos().toString() + "]"
								+ "[count :" + drawer.getDrawer(i).getStoredItemCount() + "]"
										+ "[" + ((drawer.getDrawer(i).getStoredItemCopy() != null) ?
												((drawer.getDrawer(i).getStoredItemCopy().hasDisplayName() != true)  ? 
												drawer.getDrawer(i).getStoredItemCopy().getDisplayName() : "null") : "<Empty>")   + "]");
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onExplode(ExplosionEvent event)
	{
		World world = event.getWorld();
		Iterator iter = event.getExplosion().getAffectedBlockPositions().iterator();
		while (iter.hasNext())
		{
			BlockPos pos = (BlockPos) iter.next();
		if (world.getTileEntity(pos) instanceof TileEntityDrawersStandard)
		{
			TileEntityDrawersStandard drawer = (TileEntityDrawersStandard) world.getTileEntity(pos);
			for (int i = 0 ; drawer.getDrawerCount() > i; i++)
			{
				if (drawer.getDrawer(i).getStoredItemCount() > 1 || drawer.getDrawer(i).getStoredItemCopy() != null)
				{
					if (drawer.getDrawer(i).getStoredItemCopy() != null)
					{
						if (StorageDrawers.itemTracer.containsKey(drawer.getDrawer(i).getStoredItemCopy().getItem()))
						{
							if (StorageDrawers.itemTracer.get(drawer.getDrawer(i).getStoredItemCopy().getItem()) <= drawer.getDrawer(i).getStoredItemCount())
							{
								
								iter.remove();
								if (!StorageDrawers.blocklock.contains(pos))
								{
								LogWriter.warn("[" + event.getExplosion().getExplosivePlacedBy().getName() + "]" + "["+ pos + "] drawer lock at this position");
								StorageDrawers.blocklock.add(pos);
								}
							}
						}
					}
					
				}
			}
		}
		}
	}
	
	@SubscribeEvent
	public void onBreakDrawer(BlockEvent.BreakEvent event)
	{
		World world = event.getWorld();
		if (world.getTileEntity(event.getPos()) instanceof TileEntityDrawersStandard)
		{
			TileEntityDrawersStandard drawer = (TileEntityDrawersStandard) world.getTileEntity(event.getPos());
			for (int i = 0 ; drawer.getDrawerCount() > i; i++)
			{
				if (drawer.getDrawer(i).getStoredItemCount() > 1 || drawer.getDrawer(i).getStoredItemCopy() != null)
				{
					if (drawer.getDrawer(i).getStoredItemCopy() != null)
					{
						if (StorageDrawers.itemTracer.containsKey(drawer.getDrawer(i).getStoredItemCopy().getItem()))
						{
							if (StorageDrawers.itemTracer.get(drawer.getDrawer(i).getStoredItemCopy().getItem()) <= drawer.getDrawer(i).getStoredItemCount())
							{
								event.setCanceled(true);
								if (!StorageDrawers.blocklock.contains(event.getPos()))
								{
								LogWriter.warn("[" + event.getPlayer().getName() + "]" + "["+ event.getPos().toString() + "] drawer lock at this position");
								StorageDrawers.blocklock.add(event.getPos());
								}
							}
						}
					}
					
				}
			}
		}
	}
	

}
