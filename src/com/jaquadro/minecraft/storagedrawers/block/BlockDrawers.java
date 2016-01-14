package com.jaquadro.minecraft.storagedrawers.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.item.ItemTrim;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.IResource;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockDrawers extends BlockContainer implements IExtendedBlockClickHandler, INetworked
{
    private static final ResourceLocation blockConfig = new ResourceLocation(StorageDrawers.MOD_ID + ":textures/blocks/block_config.mcmeta");

    public final boolean halfDepth;
    public final int drawerCount;

    private float trimWidth = 0.0625f;
    private float trimDepth = 0.0625f;
    private float indStart = 0;
    private float indEnd = 0;
    private int indSteps = 0;

    private String blockConfigName;

    @SideOnly(Side.CLIENT)
    protected IIcon[] iconSide;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconSideV;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconSideH;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconFront1;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconFront2;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconFront4;
    @SideOnly(Side.CLIENT)
    protected IIcon[] iconTrim;

    @SideOnly(Side.CLIENT)
    private IIcon[] iconOverlay;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconOverlayV;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconOverlayH;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconOverlayTrim;

    @SideOnly(Side.CLIENT)
    private IIcon[] iconIndicator1;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconIndicator2;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconIndicator4;

    @SideOnly(Side.CLIENT)
    private IIcon iconLock;
    @SideOnly(Side.CLIENT)
    private IIcon iconVoid;

    @SideOnly(Side.CLIENT)
    private IIcon iconTaped;

    private long ignoreEventTime;

    public BlockDrawers (String blockName, int drawerCount, boolean halfDepth) {
        this(Material.wood, blockName, drawerCount, halfDepth);
    }

    protected BlockDrawers (Material material, String blockName, int drawerCount, boolean halfDepth) {
        super(material);

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;
        this.useNeighborBrightness = true;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setStepSound(Block.soundTypeWood);
        setBlockName(blockName);
        setConfigName(blockName);
        setLightOpacity(255);
    }

    public boolean retrimBlock (World world, int x, int y, int z, ItemStack prototype) {
        if (retrimType() == null)
            return false;

        Block protoBlock = Block.getBlockFromItem(prototype.getItem());
        int protoMeta = prototype.getItemDamage();

        BlockConfiguration config = BlockConfiguration.by(retrimType(), drawerCount, halfDepth);

        Block plankBlock = StorageDrawers.blockRegistry.getPlankBlock(BlockConfiguration.Trim, protoBlock, protoMeta);
        int plankMeta = StorageDrawers.blockRegistry.getPlankMeta(BlockConfiguration.Trim, protoBlock, protoMeta);

        Block newBlock = StorageDrawers.blockRegistry.getBlock(config, plankBlock, plankMeta);
        int newMeta = StorageDrawers.blockRegistry.getMeta(config, plankBlock, plankMeta);

        if (newBlock == null)
            return false;

        if (newBlock == this)
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
        else {
            TileEntity tile = world.getTileEntity(x, y, z);
            TileEntity newDrawer = createNewTileEntity(world, newMeta);

            NBTTagCompound tag = new NBTTagCompound();
            tile.writeToNBT(tag);
            newDrawer.readFromNBT(tag);

            world.removeTileEntity(x, y, z);
            world.setBlockToAir(x, y, z);

            world.setBlock(x, y, z, newBlock, newMeta, 3);
            world.setTileEntity(x, y, z, newDrawer);
        }

        return true;
    }

    protected BlockType retrimType () {
        return BlockType.Drawers;
    }

    public float getTrimWidth () {
        return trimWidth;
    }

    public float getTrimDepth () {
        return trimDepth;
    }

    public float getIndStart () {
        return indStart;
    }

    public float getIndEnd () {
        return indEnd;
    }

    public int getIndSteps () {
        return indSteps;
    }

    public BlockDrawers setConfigName (String name) {
        blockConfigName = name;
        return this;
    }

    public String getConfigName () {
        return blockConfigName;
    }

    @Override
    public boolean renderAsNormalBlock () {
        return false;
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public int getRenderType () {
        return StorageDrawers.proxy.drawersRenderID;
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntityDrawers tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
            return;
        }

        float depth = halfDepth ? .5f : 1;
        switch (tile.getDirection()) {
            case 2:
                setBlockBounds(0, 0, 1 - depth, 1, 1, 1);
                break;
            case 3:
                setBlockBounds(0, 0, 0, 1, 1, depth);
                break;
            case 4:
                setBlockBounds(1 - depth, 0, 0, 1, 1, 1);
                break;
            case 5:
                setBlockBounds(0, 0, 0, depth, 1, 1);
                break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender () {
        if (halfDepth)
            setBlockBounds(0, 0, 0, 1, 1, .5f);
        else
            setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        TileEntityDrawers tile = getTileEntitySafe(world, x, y, z);
        if (tile.getDirection() > 1)
            return;

        int quadrant = MathHelper.floor_double((entity.rotationYaw * 4f / 360f) + .5) & 3;
        switch (quadrant) {
            case 0:
                tile.setDirection(2);
                break;
            case 1:
                tile.setDirection(5);
                break;
            case 2:
                tile.setDirection(3);
                break;
            case 3:
                tile.setDirection(4);
                break;
        }

        if (itemStack.hasDisplayName())
            tile.setInventoryName(itemStack.getDisplayName());

        if (world.isRemote) {
            tile.invalidate();
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote && Minecraft.getMinecraft().getSystemTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return false;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        ItemStack item = player.inventory.getCurrentItem();

        if (StorageDrawers.config.cache.debugTrace) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockActivated");
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());
        }

        if (item != null && item.getItem() != null) {
            if (item.getItem() instanceof ItemTrim && player.isSneaking()) {
                if (!retrimBlock(world, x, y, z, item))
                    return false;

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgrade || item.getItem() == ModItems.upgradeStatus || item.getItem() == ModItems.upgradeVoid || item.getItem() == ModItems.upgradeCreative) {
                if (!tileDrawers.addUpgrade(item)) {
                    player.addChatMessage(new ChatComponentTranslation("storagedrawers.msg.maxUpgrades"));
                    return false;
                }

                world.markBlockForUpdate(x, y, z);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeLock) {
                boolean locked = tileDrawers.isLocked(LockAttribute.LOCK_POPULATED);
                tileDrawers.setLocked(LockAttribute.LOCK_POPULATED, !locked);
                tileDrawers.setLocked(LockAttribute.LOCK_EMPTY, !locked);

                return true;
            }
            else if (item.getItem() == ModItems.shroudKey) {
                tileDrawers.setIsShrouded(!tileDrawers.isShrouded());
                return true;
            }
            else if (item.getItem() == ModItems.tape)
                return false;
        }
        else if (item == null && player.isSneaking()) {
            if (tileDrawers.isSealed()) {
                tileDrawers.setIsSealed(false);
                return true;
            }
            else if (StorageDrawers.config.cache.enableDrawerUI) {
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, x, y, z);
                return true;
            }
        }

        if (tileDrawers.getDirection() != side)
            return false;

        if (tileDrawers.isSealed())
            return false;

        int slot = getDrawerSlot(side, hitX, hitY, hitZ);
        int countAdded = tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (countAdded > 0)
            world.markBlockForUpdate(x, y, z);

        return true;
    }

    protected int getDrawerSlot (int side, float hitX, float hitY, float hitZ) {
        if (drawerCount == 1)
            return 0;
        if (drawerCount == 2)
            return hitTop(hitY) ? 0 : 1;

        if (hitLeft(side, hitX, hitZ))
            return hitTop(hitY) ? 0 : 1;
        else
            return hitTop(hitY) ? 2 : 3;
    }

    protected boolean hitTop (float hitY) {
        return hitY > .5;
    }

    protected boolean hitLeft (int side, float hitX, float hitZ) {
        switch (side) {
            case 2:
                return hitX > .5;
            case 3:
                return hitX < .5;
            case 4:
                return hitZ < .5;
            case 5:
                return hitZ > .5;
            default:
                return true;
        }
    }

    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player) {
        if (world.isRemote) {
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;
            float hitX = (float)(posn.hitVec.xCoord - posn.blockX);
            float hitY = (float)(posn.hitVec.yCoord - posn.blockY);
            float hitZ = (float)(posn.hitVec.zCoord - posn.blockZ);

            StorageDrawers.network.sendToServer(new BlockClickMessage(x, y, z, posn.sideHit, hitX, hitY, hitZ, StorageDrawers.config.cache.invertShift));

            if (StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockClicked with " + posn.toString());
        }
    }

    @Override
    public void onBlockClicked (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ, boolean invertShift) {
        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "IExtendedBlockClickHandler.onBlockClicked");

        if (!player.capabilities.isCreativeMode) {
            PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, x, y, z, side, world);
            if (event.isCanceled())
                return;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        if (tileDrawers.getDirection() != side)
            return;

        if (tileDrawers.isSealed())
            return;

        int slot = getDrawerSlot(side, hitX, hitY, hitZ);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item = null;
        // if invertSHift is true this will happen when the player is not shifting
        if (player.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());

        if (item != null && item.stackSize > 0) {
            dropItemStack(world, x, y, z, player, item);
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean rotateBlock (World world, int x, int y, int z, ForgeDirection axis) {
        TileEntityDrawers tile = getTileEntitySafe(world, x, y, z);
        if (tile.isSealed()) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
            return true;
        }

        if (tile.getDirection() == axis.ordinal())
            return false;

        if (axis == ForgeDirection.UP || axis == ForgeDirection.DOWN)
            return false;

        tile.setDirection(axis.ordinal());

        world.markBlockForUpdate(x, y, z);

        if (world.isRemote)
            ignoreEventTime = Minecraft.getMinecraft().getSystemTime();

        return true;
    }

    @Override
    public boolean isSideSolid (IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (halfDepth)
            return false;

        if (side == ForgeDirection.DOWN) {
            Block blockUnder = world.getBlock(x, y - 1, z);
            if (blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return false;
        }

        if (getTileEntity(world, x, y, z) == null)
            return true;
        if (side.ordinal() != getTileEntity(world, x, y, z).getDirection())
            return true;

        return false;
    }

    private void dropItemStack (World world, int x, int y, int z, EntityPlayer player, ItemStack stack) {
        EntityItem entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
        world.spawnEntityInWorld(entity);
    }

    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z) {
        if (world.isRemote && player.capabilities.isCreativeMode) {
            TileEntityDrawers tile = getTileEntity(world, x, y, z);
            MovingObjectPosition posn = Minecraft.getMinecraft().objectMouseOver;

            if (tile.getDirection() == posn.sideHit) {
                onBlockClicked(world, x, y, z, player);
                return false;
            }
        }

        return super.removedByPlayer(world, player, x, y, z);
    }

    @Override
    public void breakBlock (World world, int x, int y, int z, Block block, int meta) {
        TileEntityDrawers tile = getTileEntity(world, x, y, z);

        if (tile != null && !tile.isSealed()) {
            for (int i = 0; i < tile.getUpgradeSlotCount(); i++) {
                ItemStack stack = tile.getUpgrade(i);
                if (stack != null)
                    dropBlockAsItem(world, x, y, z, stack);
            }

            if (!tile.isVending()) {
                for (int i = 0; i < tile.getDrawerCount(); i++) {
                    if (!tile.isDrawerEnabled(i))
                        continue;

                    IDrawer drawer = tile.getDrawer(i);
                    while (drawer.getStoredItemCount() > 0) {
                        ItemStack stack = tile.takeItemsFromSlot(i, drawer.getStoredItemStackSize());
                        if (stack == null || stack.stackSize == 0)
                            break;

                        dropStackInBatches(world, x, y, z, stack);
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    private void dropStackInBatches (World world, int x, int y, int z, ItemStack stack) {
        Random rand = world.rand;

        float ex = rand.nextFloat() * .8f + .1f;
        float ey = rand.nextFloat() * .8f + .1f;
        float ez = rand.nextFloat() * .8f + .1f;

        EntityItem entity;
        for (; stack.stackSize > 0; world.spawnEntityInWorld(entity)) {
            int stackPartSize = rand.nextInt(21) + 10;
            if (stackPartSize > stack.stackSize)
                stackPartSize = stack.stackSize;

            stack.stackSize -= stackPartSize;
            entity = new EntityItem(world, x + ex, y + ey, z + ez, new ItemStack(stack.getItem(), stackPartSize, stack.getItemDamage()));

            float motionUnit = .05f;
            entity.motionX = rand.nextGaussian() * motionUnit;
            entity.motionY = rand.nextGaussian() * motionUnit + .2f;
            entity.motionZ = rand.nextGaussian() * motionUnit;

            if (stack.hasTagCompound())
                entity.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
        }
    }

    @Override
    public int damageDropped (int meta) {
        return meta;
    }

    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest)
            return true;

        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops (World world, int x, int y, int z, int metadata, int fortune) {
        ItemStack dropStack = new ItemStack(Item.getItemFromBlock(this), 1, metadata);

        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(dropStack);

        TileEntityDrawers tile = getTileEntity(world, x, y, z);
        if (tile == null || !tile.isSealed())
            return drops;

        NBTTagCompound tiledata = new NBTTagCompound();
        tile.writeToNBT(tiledata);

        NBTTagCompound data = new NBTTagCompound();
        data.setTag("tile", tiledata);

        dropStack.setTagCompound(data);

        return drops;
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawersStandard();
    }

    public TileEntityDrawers getTileEntity (IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, int x, int y, int z) {
        TileEntityDrawers tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            tile = (TileEntityDrawers)createNewTileEntity(world, world.getBlockMetadata(x, y, z));
            world.setTileEntity(x, y, z, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects (World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        TileEntity tile = worldObj.getTileEntity(target.blockX, target.blockY, target.blockZ);
        if (tile instanceof TileEntityDrawers) {
            if (((TileEntityDrawers) tile).getDirection() == target.sideHit)
                return true;
        }

        return super.addHitEffects(worldObj, target, effectRenderer);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects (World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityDrawers)
        return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }*/

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));

        if (StorageDrawers.config.cache.creativeTabVanillaWoods) {
            for (int i = 1; i < BlockWood.field_150096_a.length; i++)
                list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim (int meta) {
        meta %= iconTrim.length;
        return iconTrim[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        meta %= iconSide.length;

        switch (side) {
            case 0:
            case 1:
                return halfDepth ? iconSideH[meta] : iconSide[meta];
            case 2:
            case 3:
                return halfDepth ? iconSideV[meta] : iconSide[meta];
            case 4:
                switch (drawerCount) {
                    case 1: return iconFront1[meta];
                    case 2: return iconFront2[meta];
                    case 4: return iconFront4[meta];
                }
                return null;
            case 5:
                return iconSide[meta];
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side) {
        return getIcon(blockAccess, x, y, z, side, 0);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getOverlayIcon (IBlockAccess blockAccess, int x, int y, int z, int side, int level) {
        if (level == 0)
            return null;

        return getIcon(blockAccess, x, y, z, side, level);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getOverlayIconTrim (int level) {
        if (level == 0)
            return null;

        return iconOverlayTrim[level];
    }

    @SideOnly(Side.CLIENT)
    protected IIcon getIcon (IBlockAccess blockAccess, int x, int y, int z, int side, int level) {
        int meta = blockAccess.getBlockMetadata(x, y, z) % iconSide.length;

        TileEntityDrawers tile = getTileEntity(blockAccess, x, y, z);
        if (tile == null || side == tile.getDirection()) {
            if (drawerCount == 1)
                return  iconFront1[meta];
            else if (drawerCount == 2)
                return iconFront2[meta];
            else
                return iconFront4[meta];
        }

        switch (side) {
            case 0:
            case 1:
                if (halfDepth) {
                    switch (tile.getDirection()) {
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            return (level > 0) ? iconOverlayH[level] : iconSideH[meta];
                    }
                }
                break;
            case 2:
            case 3:
                if (halfDepth) {
                    switch (tile.getDirection()) {
                        case 2:
                        case 3:
                            return (level > 0) ? iconOverlay[level] : iconSide[meta];
                        case 4:
                        case 5:
                            return (level > 0) ? iconOverlayV[level] : iconSideV[meta];
                    }
                }
                break;
            case 4:
            case 5:
                if (halfDepth) {
                    switch (tile.getDirection()) {
                        case 2:
                        case 3:
                            return (level > 0) ? iconOverlayV[level] : iconSideV[meta];
                        case 4:
                        case 5:
                            return (level > 0) ? iconOverlay[level] : iconSide[meta];
                    }
                }
                break;
        }

        return (level > 0) ? iconOverlay[level] : iconSide[meta];
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIndicatorIcon (int drawerCount, boolean on) {
        int onIndex = on ? 1 : 0;
        switch (drawerCount) {
            case 1: return iconIndicator1[onIndex];
            case 2: return iconIndicator2[onIndex];
            case 4: return iconIndicator4[onIndex];
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getLockIcon () {
        return iconLock;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getVoidIcon () {
        return iconVoid;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getTapeIcon () { return iconTaped; }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister register) {
        String[] subtex = BlockWood.field_150096_a;

        iconSide = new IIcon[subtex.length];
        iconSideH = new IIcon[subtex.length];
        iconSideV = new IIcon[subtex.length];
        iconFront1 = new IIcon[subtex.length];
        iconFront2 = new IIcon[subtex.length];
        iconFront4 = new IIcon[subtex.length];
        iconTrim = new IIcon[subtex.length];

        for (int i = 0; i < subtex.length; i++) {
            iconFront1[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_1");
            iconFront2[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_2");
            iconFront4[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_front_4");
            iconSide[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side");
            iconSideV[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side_v");
            iconSideH[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_side_h");
            iconTrim[i] = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_" + subtex[i] + "_trim");
        }

        iconTaped = register.registerIcon(StorageDrawers.MOD_ID + ":tape");

        String[] overlays = new String[] { null, null, "iron", "gold", "obsidian", "diamond", "emerald" };

        iconOverlay = new IIcon[overlays.length];
        iconOverlayH = new IIcon[overlays.length];
        iconOverlayV = new IIcon[overlays.length];
        iconOverlayTrim = new IIcon[overlays.length];

        for (int i = 2; i < overlays.length; i++) {
            iconOverlay[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i]);
            iconOverlayV[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_v");
            iconOverlayH[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_h");
            iconOverlayTrim[i] = register.registerIcon(StorageDrawers.MOD_ID + ":overlay_" + overlays[i] + "_trim");
        }

        iconIndicator1 = new IIcon[2];
        iconIndicator2 = new IIcon[2];
        iconIndicator4 = new IIcon[2];

        iconIndicator1[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_1_off");
        iconIndicator1[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_1_on");
        iconIndicator2[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_2_off");
        iconIndicator2[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_2_on");
        iconIndicator4[0] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_4_off");
        iconIndicator4[1] = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/indicator_4_on");

        iconLock = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/lock_icon");
        iconVoid = register.registerIcon(StorageDrawers.MOD_ID + ":indicator/void_icon");

        loadBlockConfig();
    }

    @SideOnly(Side.CLIENT)
    private void loadBlockConfig () {
        try {
            IResource configResource = Minecraft.getMinecraft().getResourceManager().getResource(blockConfig);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(configResource.getInputStream()));
                JsonObject root = (new JsonParser()).parse(reader).getAsJsonObject();

                JsonObject entry = root.getAsJsonObject(getConfigName());
                if (entry != null) {
                    if (entry.has("trimWidth"))
                        trimWidth = entry.get("trimWidth").getAsFloat();
                    if (entry.has("trimDepth"))
                        trimDepth = entry.get("trimDepth").getAsFloat();
                    if (entry.has("indStart"))
                        indStart = entry.get("indStart").getAsFloat();
                    if (entry.has("indEnd"))
                        indEnd = entry.get("indEnd").getAsFloat();
                    if (entry.has("indSteps"))
                        indSteps = entry.get("indSteps").getAsInt();
                }
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
