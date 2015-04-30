package com.jaquadro.minecraft.storagedrawers.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
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
import java.util.List;
import java.util.Random;

public class BlockDrawers extends BlockContainer implements IExtendedBlockClickHandler, INetworked
{
    private static final ResourceLocation blockConfig = new ResourceLocation(StorageDrawers.MOD_ID + ":textures/blocks/block_config.mcmeta");

    public final boolean halfDepth;
    public final int drawerCount;

    public float trimWidth = 0.0625f;
    public float trimDepth = 0.0625f;
    public float indStart = 0;
    public float indEnd = 0;
    public int indSteps = 0;

    @SideOnly(Side.CLIENT)
    private IIcon[] iconSide;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconSideV;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconSideH;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconFront1;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconFront2;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconFront4;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconTrim;

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

    public BlockDrawers (String blockName, int drawerCount, boolean halfDepth) {
        this(Material.wood, blockName, drawerCount, halfDepth);
    }

    protected BlockDrawers (Material material, String blockName, int drawerCount, boolean halfDepth) {
        super(material);

        this.drawerCount = drawerCount;
        this.halfDepth = halfDepth;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setStepSound(Block.soundTypeWood);
        setBlockName(blockName);
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

        if (world.isRemote) {
            tile.invalidate();
            world.markBlockForUpdate(x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityDrawers tileDrawers = getTileEntitySafe(world, x, y, z);
        ItemStack item = player.inventory.getCurrentItem();

        if (StorageDrawers.config.cache.debugTrace) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockActivated");
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());
        }

        if (item != null && item.getItem() != null) {
            if (item.getItem() == ModItems.upgrade  && item.getItemDamage() != tileDrawers.getStorageLevel()) {
                tileDrawers.setStorageLevel(item.getItemDamage());
                world.markBlockForUpdate(x, y, z);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeStatus && item.getItemDamage() != tileDrawers.getStatusLevel()) {
                tileDrawers.setStatusLevel(item.getItemDamage());
                world.markBlockForUpdate(x, y, z);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeLock) {
                tileDrawers.setIsLocked(!tileDrawers.isLocked());
                world.markBlockForUpdate(x, y, z);

                return true;
            }
            else if (item.getItem() == ModItems.upgradeVoid && !tileDrawers.isVoid()) {
                tileDrawers.setVoid(true);
                world.markBlockForUpdate(x, y, z);

                if (player != null && !player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
        }

        if (tileDrawers.getDirection() != side)
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

        if (tile != null) {
            if (tile.getStorageLevel() > 1)
                dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.upgrade, 1, tile.getStorageLevel()));
            if (tile.getStatusLevel() > 0)
                dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.upgradeStatus, 1, tile.getStatusLevel()));
            if (tile.isLocked())
                dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.upgradeLock));

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
        meta %= BlockWood.field_150096_a.length;
        return iconTrim[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta) {
        meta %= BlockWood.field_150096_a.length;

        switch (side) {
            case 0:
            case 1:
                return halfDepth ? iconSideV[meta] : iconSide[meta];
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
        int meta = blockAccess.getBlockMetadata(x, y, z) % BlockWood.field_150096_a.length;

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
                            return (level > 0) ? iconOverlayH[level] : iconSideH[meta];
                        case 4:
                        case 5:
                            return (level > 0) ? iconOverlayV[level] : iconSideV[meta];
                    }
                }
                break;
            default:
                if (halfDepth)
                    return (level > 0) ? iconOverlayV[level] : iconSideV[meta];
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

    private void loadBlockConfig () {
        try {
            IResource configResource = Minecraft.getMinecraft().getResourceManager().getResource(blockConfig);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(configResource.getInputStream()));
                JsonObject root = (new JsonParser()).parse(reader).getAsJsonObject();

                JsonObject entry = root.getAsJsonObject(getUnlocalizedName().substring(5));
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
