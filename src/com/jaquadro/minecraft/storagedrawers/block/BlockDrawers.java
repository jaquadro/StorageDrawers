package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedTileEntity;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerInventoryHelper;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import com.jaquadro.minecraft.storagedrawers.item.*;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;

import com.jaquadro.minecraft.storagedrawers.network.BlockDestroyMessage;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class BlockDrawers extends BlockContainer implements IExtendedBlockClickHandler, IBlockDestroyHandler, INetworked
{
    public static final PropertyEnum BLOCK = PropertyEnum.create("block", EnumBasicDrawer.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

    public static final IUnlistedProperty<TileEntityDrawers> TILE = UnlistedTileEntity.create(TileEntityDrawers.class);

    private static final AxisAlignedBB AABB_NORTH_HALF = new AxisAlignedBB(0, 0, .5, 1, 1, 1);
    private static final AxisAlignedBB AABB_SOUTH_HALF = new AxisAlignedBB(0, 0, 0, 1, 1, .5);
    private static final AxisAlignedBB AABB_WEST_HALF = new AxisAlignedBB(.5, 0, 0, 1, 1, 1);
    private static final AxisAlignedBB AABB_EAST_HALF = new AxisAlignedBB(0, 0, 0, .5, 1, 1);

    @SideOnly(Side.CLIENT)
    private StatusModelData[] statusInfo;

    private long ignoreEventTime;

    public BlockDrawers (String blockName) {
        this(Material.WOOD, blockName);
    }

    protected BlockDrawers (Material material, String blockName) {
        super(material);

        this.useNeighborBrightness = true;

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setSoundType(SoundType.WOOD);
        setUnlocalizedName(blockName);
        setRegistryName(blockName);
        setLightOpacity(255);

        initDefaultState();
    }

    protected void initDefaultState () {
        setDefaultState(blockState.getBaseState().withProperty(BLOCK, EnumBasicDrawer.FULL2));
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public boolean retrimBlock (World world, BlockPos pos, ItemStack prototype) {
        if (retrimType() == null)
            return false;

        IBlockState curState = getActualState(world.getBlockState(pos), world, pos);
        if (curState == null || !(curState.getBlock() instanceof BlockDrawers))
            return false;

        Block protoBlock = Block.getBlockFromItem(prototype.getItem());
        int protoMeta = prototype.getItemDamage();

        IBlockState newState = protoBlock.getStateFromMeta(protoMeta);
        if (newState == null || !(newState.getBlock() instanceof BlockTrim))
            return false;

        BlockPlanks.EnumType curVariant = (BlockPlanks.EnumType)curState.getValue(VARIANT);
        BlockPlanks.EnumType newVariant = (BlockPlanks.EnumType)newState.getValue(VARIANT);
        if (curVariant == newVariant)
            return false;

        TileEntityDrawers tile = getTileEntity(world, pos);
        tile.setMaterial(newVariant.getName());

        world.setBlockState(pos, curState.withProperty(VARIANT, newVariant));

        return true;
    }

    public BlockType retrimType () {
        return BlockType.Drawers;
    }

    public int getDrawerCount (IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockDrawers) {
            EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BLOCK);
            if (info != null)
                return info.getDrawerCount();
        }

        return 0;
    }

    public boolean isHalfDepth (IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockDrawers) {
            EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BLOCK);
            if (info != null)
                return info.isHalfDepth();
        }

        return false;
    }

    public EnumFacing getDirection (IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        return (tile != null) ? EnumFacing.getFront(tile.getDirection()) : EnumFacing.NORTH;
    }

    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        statusInfo = new StatusModelData[EnumBasicDrawer.values().length];
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/basicDrawers_" + type.getName() + ".json");
            statusInfo[type.getMetadata()] = new StatusModelData(type.getDrawerCount(), location);
        }
    }

    @SideOnly(Side.CLIENT)
    public StatusModelData getStatusInfo (IBlockState state) {
        if (state != null) {
            EnumBasicDrawer info = (EnumBasicDrawer) state.getValue(BLOCK);
            if (info != null)
                return statusInfo[info.getMetadata()];
        }

        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube (IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube (IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType (IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer () {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(blockAccess, pos);
        if (tile != null && isHalfDepth(state)) {
            switch (EnumFacing.getFront(tile.getDirection())) {
                case NORTH:
                    return AABB_NORTH_HALF;
                case SOUTH:
                    return AABB_SOUTH_HALF;
                case WEST:
                    return AABB_WEST_HALF;
                case EAST:
                    return AABB_EAST_HALF;
            }
        }

        return FULL_BLOCK_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList (IBlockState state, World world, BlockPos pos, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity) {
        addCollisionBoxToList(pos, aabb, list, getBoundingBox(state, world, pos));
    }

    @Override
    public void onBlockAdded (World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            IBlockState blockNorth = world.getBlockState(pos.north());
            IBlockState blockSouth = world.getBlockState(pos.south());
            IBlockState blockWest = world.getBlockState(pos.west());
            IBlockState blockEast = world.getBlockState(pos.east());

            EnumFacing facing = state.getValue(FACING);

            if (facing == EnumFacing.NORTH && blockNorth.isFullBlock() && !blockSouth.isFullBlock())
                facing = EnumFacing.SOUTH;
            if (facing == EnumFacing.SOUTH && blockSouth.isFullBlock() && !blockNorth.isFullBlock())
                facing = EnumFacing.NORTH;
            if (facing == EnumFacing.WEST && blockWest.isFullBlock() && !blockEast.isFullBlock())
                facing = EnumFacing.EAST;
            if (facing == EnumFacing.EAST && blockEast.isFullBlock() && !blockWest.isFullBlock())
                facing = EnumFacing.WEST;

            TileEntityDrawers tile = getTileEntitySafe(world, pos);
            tile.setDirection(facing.ordinal());
            tile.markDirty();

            world.setBlockState(pos, state.withProperty(FACING, facing));
        }

        super.onBlockAdded(world, pos, state);
    }

    @Override
    public IBlockState onBlockPlaced (World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
    }

    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        EnumFacing facing = entity.getHorizontalFacing().getOpposite();

        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        tile.setDirection(facing.ordinal());
        tile.markDirty();

        if (itemStack.hasDisplayName())
            tile.setInventoryName(itemStack.getDisplayName());

        world.setBlockState(pos, state.withProperty(FACING, facing), 3);
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack item, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote && Minecraft.getSystemTime() == ignoreEventTime) {
            ignoreEventTime = 0;
            return false;
        }

        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);

        if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
            return false;

        if (StorageDrawers.config.cache.debugTrace) {
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockActivated");
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());
        }

        if (item != null && item.getItem() != null) {
            if (item.getItem() instanceof ItemTrim && player.isSneaking()) {
                if (!retrimBlock(world, pos, item))
                    return false;

                if (!player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.upgradeStorage || item.getItem() == ModItems.upgradeStatus || item.getItem() == ModItems.upgradeVoid ||
               item.getItem() == ModItems.upgradeCreative || item.getItem() == ModItems.upgradeRedstone) {
                if (!tileDrawers.addUpgrade(item) && !world.isRemote) {
                    player.addChatMessage(new TextComponentTranslation("storagedrawers.msg.maxUpgrades"));
                    return false;
                }

                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.capabilities.isCreativeMode) {
                    if (--item.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                return true;
            }
            else if (item.getItem() == ModItems.drawerKey) {
                boolean locked = tileDrawers.isItemLocked(LockAttribute.LOCK_POPULATED);
                tileDrawers.setItemLocked(LockAttribute.LOCK_POPULATED, !locked);
                tileDrawers.setItemLocked(LockAttribute.LOCK_EMPTY, !locked);

                return true;
            }
            else if (item.getItem() == ModItems.shroudKey) {
                tileDrawers.setIsShrouded(!tileDrawers.isShrouded());
                return true;
            }
            else if (item.getItem() instanceof ItemPersonalKey) {
                String securityKey = ((ItemPersonalKey) item.getItem()).getSecurityProviderKey(item.getItemDamage());
                ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

                if (tileDrawers.getOwner() == null) {
                    tileDrawers.setOwner(player.getPersistentID());
                    tileDrawers.setSecurityProvider(provider);
                }
                else if (SecurityManager.hasOwnership(player.getGameProfile(), tileDrawers)) {
                    tileDrawers.setOwner(null);
                    tileDrawers.setSecurityProvider(null);
                }
                else
                    return false;
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
                player.openGui(StorageDrawers.instance, GuiHandler.drawersGuiID, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        if (tileDrawers.getDirection() != side.ordinal())
            return false;

        if (tileDrawers.isSealed())
            return false;

        int slot = getDrawerSlot(getDrawerCount(state), side.ordinal(), hitX, hitY, hitZ);
        int countAdded = tileDrawers.interactPutItemsIntoSlot(slot, player);

        if (countAdded > 0)
            world.notifyBlockUpdate(pos, state, state, 3);

        return true;
    }

    protected int getDrawerSlot (int drawerCount, int side, float hitX, float hitY, float hitZ) {
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
    public void onBlockDestroyed (final World world, final BlockPos pos) {
        if (world.isRemote)
            return;

        ((WorldServer)world).addScheduledTask(new Runnable()
        {
            @Override
            public void run () {
                BlockDrawers.this.onBlockDestroyedAsync(world, pos);
            }
        });
    }

    private void onBlockDestroyedAsync (World world, BlockPos pos) {
        //this.onBlockHarvested(world, pos, state, player);
        world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
    }

    @Override
    public void onBlockClicked (World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote) {
            RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
            BlockPos posb = ray.getBlockPos();
            float hitX = (float)(ray.hitVec.xCoord - posb.getX());
            float hitY = (float)(ray.hitVec.yCoord - posb.getY());
            float hitZ = (float)(ray.hitVec.zCoord - posb.getZ());

            StorageDrawers.network.sendToServer(new BlockClickMessage(pos.getX(), pos.getY(), pos.getZ(), ray.sideHit.ordinal(), hitX, hitY, hitZ, StorageDrawers.config.cache.invertShift));

            if (StorageDrawers.config.cache.debugTrace)
                FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockClicked with " + ray.toString());
        }
    }

    @Override
    public void onBlockClicked (final World world, final BlockPos pos, final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY, final float hitZ, final boolean invertShift) {
        if (world.isRemote)
            return;

        ((WorldServer)world).addScheduledTask(new Runnable()
        {
            @Override
            public void run () {
                BlockDrawers.this.onBlockClickedAsync(world, pos, player, side, hitX, hitY, hitZ, invertShift);
            }
        });
    }

    private void onBlockClickedAsync (World world, BlockPos pos, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, boolean invertShift) {
        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "IExtendedBlockClickHandler.onBlockClicked");

        if (!player.capabilities.isCreativeMode) {
            PlayerInteractEvent.LeftClickBlock event = new PlayerInteractEvent.LeftClickBlock(player, pos, side, new Vec3d(hitX, hitY, hitZ));
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled())
                return;
        }
        TileEntityDrawers tileDrawers = getTileEntitySafe(world, pos);
        if (tileDrawers.getDirection() != side.ordinal())
            return;

        if (tileDrawers.isSealed())
            return;

        if (!SecurityManager.hasAccess(player.getGameProfile(), tileDrawers))
            return;

        int slot = getDrawerSlot(getDrawerCount(world.getBlockState(pos)), side.ordinal(), hitX, hitY, hitZ);
        IDrawer drawer = tileDrawers.getDrawer(slot);

        ItemStack item;
        if (player.isSneaking() != invertShift)
            item = tileDrawers.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
        else
            item = tileDrawers.takeItemsFromSlot(slot, 1);

        if (StorageDrawers.config.cache.debugTrace)
            FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item.toString());

        IBlockState state = world.getBlockState(pos);
        if (item != null && item.stackSize > 0) {
            if (!player.inventory.addItemStackToInventory(item)) {
                dropItemStack(world, pos.offset(side), player, item);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
            else if (!world.isRemote)
                world.playSound(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, .2f, ((world.rand.nextFloat() - world.rand.nextFloat()) * .7f + 1) * 2);
        }
    }

    @Override
    public boolean rotateBlock (World world, BlockPos pos, EnumFacing axis) {
        TileEntityDrawers tile = getTileEntitySafe(world, pos);
        if (tile.isSealed()) {
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            world.setBlockToAir(pos);
            return true;
        }

        return super.rotateBlock(world, pos, axis);
    }

    @Override
    public boolean isSideSolid (IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (isHalfDepth(state))
            return false;

        if (side == EnumFacing.DOWN) {
            Block blockUnder = world.getBlockState(pos.down()).getBlock();
            if (blockUnder instanceof BlockChest || blockUnder instanceof BlockEnderChest)
                return false;
        }

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return true;

        return side.ordinal() != tile.getDirection();
    }

    private void dropItemStack (World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        EntityItem entity = new EntityItem(world, pos.getX() + .5f, pos.getY() + .1f, pos.getZ() + .5f, stack);
        entity.addVelocity(-entity.motionX, -entity.motionY, -entity.motionZ);
        world.spawnEntityInWorld(entity);
    }

    @Override
    public int damageDropped (IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (player.capabilities.isCreativeMode) {
            if (world.isRemote) {
                RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;

                if (getDirection(world, pos) == ray.sideHit) {
                    onBlockClicked(world, pos, player);
                    if (StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, "BlockDrawers.onBlockClicked with " + ray.toString());
                } else {
                    StorageDrawers.network.sendToServer(new BlockDestroyMessage(pos));
                }
            }

            return false;
        }

        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntityDrawers tile = getTileEntity(world, pos);

        if (tile != null && !tile.isSealed()) {
            for (int i = 0; i < tile.getUpgradeSlotCount(); i++) {
                ItemStack stack = tile.getUpgrade(i);
                if (stack != null) {
                    if (stack.getItem() instanceof ItemUpgradeCreative)
                        continue;
                    spawnAsEntity(world, pos, stack);
                }
            }

            if (!tile.isVending())
                DrawerInventoryHelper.dropInventoryItems(world, pos, tile);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack dropStack = getMainDrop(world, pos, state);

        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(dropStack);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return drops;

        NBTTagCompound data = dropStack.getTagCompound();
        if (data == null)
            data = new NBTTagCompound();

        BlockPlanks.EnumType material = translateMaterial(tile.getMaterialOrDefault());
        data.setString("material", material.getName());

        if (tile.isSealed()) {
            NBTTagCompound tiledata = new NBTTagCompound();
            tile.writeToNBT(tiledata);
            data.setTag("tile", tiledata);
        }

        dropStack.setTagCompound(data);

        return drops;
    }

    protected ItemStack getMainDrop (IBlockAccess world, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public float getExplosionResistance (World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null) {
            for (int slot = 0; slot < 5; slot++) {
                ItemStack stack = tile.getUpgrade(slot);
                if (stack == null || !(stack.getItem() instanceof ItemUpgradeStorage))
                    continue;

                if (EnumUpgradeStorage.byMetadata(stack.getMetadata()) != EnumUpgradeStorage.OBSIDIAN)
                    continue;

                return 1000;
            }
        }

        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public TileEntityDrawers createNewTileEntity (World world, int meta) {
        return new TileEntityDrawersStandard();
    }

    public TileEntityDrawers getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntityDrawers) ? (TileEntityDrawers) tile : null;
    }

    public TileEntityDrawers getTileEntitySafe (World world, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects (IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        if (getDirection(worldObj, target.getBlockPos()) == target.sideHit)
            return true;

        return super.addHitEffects(state, worldObj, target, manager);
    }

    @Override
    public boolean addDestroyEffects (World world, BlockPos pos, ParticleManager manager) {
        //TileEntityDrawers tile = getTileEntity(world, pos);
        //if (tile != null && !tile.getWillDestroy())
        //    return true;

        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    public void getSubBlocks (Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
                ItemStack stack = new ItemStack(item, 1, type.getMetadata());

                NBTTagCompound data = new NBTTagCompound();
                data.setString("material", material.getName());
                stack.setTagCompound(data);

                if (StorageDrawers.config.cache.creativeTabVanillaWoods || material == BlockPlanks.EnumType.OAK)
                    list.add(stack);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower (IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower (IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        if (!canProvidePower(state))
            return 0;

        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if (tile == null || !tile.isRedstone())
            return 0;

        return tile.getRedstoneLevel();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower (IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return (side == EnumFacing.UP) ? getWeakPower(state, worldIn, pos, side) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta (int meta) {
        return getDefaultState().withProperty(BLOCK, EnumBasicDrawer.byMetadata(meta));
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return ((EnumBasicDrawer)state.getValue(BLOCK)).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { BLOCK, VARIANT, FACING }, new IUnlistedProperty[] { TILE });
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityDrawers tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return state;

        EnumFacing facing = EnumFacing.getFront(tile.getDirection());
        if (facing.getAxis() == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH;

        BlockPlanks.EnumType woodType = translateMaterial(tile.getMaterialOrDefault());

        return state.withProperty(BLOCK, state.getValue(BLOCK))
            .withProperty(FACING, facing)
            .withProperty(VARIANT, woodType);
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(TILE, tile);
    }

    private BlockPlanks.EnumType translateMaterial (String materal) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            if (materal.equals(type.getName()))
                return type;
        }

        return BlockPlanks.EnumType.OAK;
    }
}
