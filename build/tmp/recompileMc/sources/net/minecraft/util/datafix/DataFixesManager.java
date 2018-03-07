package net.minecraft.util.datafix;

import net.minecraft.block.BlockJukebox;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.datafix.fixes.ArmorStandSilent;
import net.minecraft.util.datafix.fixes.BookPagesStrictJSON;
import net.minecraft.util.datafix.fixes.CookedFishIDTypo;
import net.minecraft.util.datafix.fixes.EntityArmorAndHeld;
import net.minecraft.util.datafix.fixes.EntityHealth;
import net.minecraft.util.datafix.fixes.ForceVBOOn;
import net.minecraft.util.datafix.fixes.HorseSaddle;
import net.minecraft.util.datafix.fixes.ItemIntIDToString;
import net.minecraft.util.datafix.fixes.MinecartEntityTypes;
import net.minecraft.util.datafix.fixes.PaintingDirection;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraft.util.datafix.fixes.RedundantChanceTags;
import net.minecraft.util.datafix.fixes.RidingToPassengers;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.datafix.fixes.SpawnEggNames;
import net.minecraft.util.datafix.fixes.SpawnerEntityTypes;
import net.minecraft.util.datafix.fixes.StringToUUID;
import net.minecraft.util.datafix.fixes.ZombieProfToType;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.WorldInfo;

public class DataFixesManager
{
    private static void registerFixes(DataFixer fixer)
    {
        fixer.registerFix(FixTypes.ENTITY, new EntityArmorAndHeld());
        fixer.registerFix(FixTypes.BLOCK_ENTITY, new SignStrictJSON());
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new ItemIntIDToString());
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new PotionItems());
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new SpawnEggNames());
        fixer.registerFix(FixTypes.ENTITY, new MinecartEntityTypes());
        fixer.registerFix(FixTypes.BLOCK_ENTITY, new SpawnerEntityTypes());
        fixer.registerFix(FixTypes.ENTITY, new StringToUUID());
        fixer.registerFix(FixTypes.ENTITY, new EntityHealth());
        fixer.registerFix(FixTypes.ENTITY, new HorseSaddle());
        fixer.registerFix(FixTypes.ENTITY, new PaintingDirection());
        fixer.registerFix(FixTypes.ENTITY, new RedundantChanceTags());
        fixer.registerFix(FixTypes.ENTITY, new RidingToPassengers());
        fixer.registerFix(FixTypes.ENTITY, new ArmorStandSilent());
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new BookPagesStrictJSON());
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new CookedFishIDTypo());
        fixer.registerFix(FixTypes.ENTITY, new ZombieProfToType());
        fixer.registerFix(FixTypes.OPTIONS, new ForceVBOOn());
    }

    public static DataFixer createFixer()
    {
        DataFixer datafixer = new DataFixer(512);
        WorldInfo.registerFixes(datafixer);
        EntityPlayer.registerFixesPlayer(datafixer);
        AnvilChunkLoader.registerFixes(datafixer);
        ItemStack.registerFixes(datafixer);
        EntityArmorStand.registerFixesArmorStand(datafixer);
        EntityArrow.registerFixesArrow(datafixer);
        EntityBat.registerFixesBat(datafixer);
        EntityBlaze.registerFixesBlaze(datafixer);
        EntityCaveSpider.registerFixesCaveSpider(datafixer);
        EntityChicken.registerFixesChicken(datafixer);
        EntityCow.registerFixesCow(datafixer);
        EntityCreeper.registerFixesCreeper(datafixer);
        EntityDragonFireball.registerFixesDragonFireball(datafixer);
        EntityDragon.registerFixesDragon(datafixer);
        EntityEnderman.registerFixesEnderman(datafixer);
        EntityEndermite.registerFixesEndermite(datafixer);
        EntityFallingBlock.registerFixesFallingBlock(datafixer);
        EntityLargeFireball.registerFixesLargeFireball(datafixer);
        EntityFireworkRocket.registerFixesFireworkRocket(datafixer);
        EntityGhast.registerFixesGhast(datafixer);
        EntityGiantZombie.registerFixesGiantZombie(datafixer);
        EntityGuardian.registerFixesGuardian(datafixer);
        EntityHorse.registerFixesHorse(datafixer);
        EntityItem.registerFixesItem(datafixer);
        EntityItemFrame.registerFixesItemFrame(datafixer);
        EntityMagmaCube.registerFixesMagmaCube(datafixer);
        EntityMinecartChest.registerFixesMinecartChest(datafixer);
        EntityMinecartCommandBlock.registerFixesMinecartCommand(datafixer);
        EntityMinecartFurnace.registerFixesMinecartFurnace(datafixer);
        EntityMinecartHopper.registerFixesMinecartHopper(datafixer);
        EntityMinecartEmpty.registerFixesMinecartEmpty(datafixer);
        EntityMinecartMobSpawner.registerFixesMinecartMobSpawner(datafixer);
        EntityMinecartTNT.registerFixesMinecartTNT(datafixer);
        EntityLiving.registerFixesMob(datafixer);
        EntityMob.registerFixesMonster(datafixer);
        EntityMooshroom.registerFixesMooshroom(datafixer);
        EntityOcelot.registerFixesOcelot(datafixer);
        EntityPig.registerFixesPig(datafixer);
        EntityPigZombie.registerFixesPigZombie(datafixer);
        EntityRabbit.registerFixesRabbit(datafixer);
        EntitySheep.registerFixesSheep(datafixer);
        EntityShulker.registerFixesShulker(datafixer);
        EntitySilverfish.registerFixesSilverfish(datafixer);
        EntitySkeleton.registerFixesSkeleton(datafixer);
        EntitySlime.registerFixesSlime(datafixer);
        EntitySmallFireball.registerFixesSmallFireball(datafixer);
        EntitySnowman.registerFixesSnowman(datafixer);
        EntitySnowball.registerFixesSnowball(datafixer);
        EntitySpectralArrow.registerFixesSpectralArrow(datafixer);
        EntitySpider.registerFixesSpider(datafixer);
        EntitySquid.registerFixesSquid(datafixer);
        EntityEgg.registerFixesEgg(datafixer);
        EntityEnderPearl.registerFixesEnderPearl(datafixer);
        EntityExpBottle.registerFixesExpBottle(datafixer);
        EntityPotion.registerFixesPotion(datafixer);
        EntityTippedArrow.registerFixesTippedArrow(datafixer);
        EntityVillager.registerFixesVillager(datafixer);
        EntityIronGolem.registerFixesIronGolem(datafixer);
        EntityWitch.registerFixesWitch(datafixer);
        EntityWither.registerFixesWither(datafixer);
        EntityWitherSkull.registerFixesWitherSkull(datafixer);
        EntityWolf.registerFixesWolf(datafixer);
        EntityZombie.registerFixesZombie(datafixer);
        TileEntityPiston.registerFixesPiston(datafixer);
        TileEntityFlowerPot.registerFixesFlowerPot(datafixer);
        TileEntityFurnace.registerFixesFurnace(datafixer);
        TileEntityChest.registerFixesChest(datafixer);
        TileEntityDispenser.registerFixes(datafixer);
        TileEntityDropper.registerFixesDropper(datafixer);
        TileEntityBrewingStand.registerFixesBrewingStand(datafixer);
        TileEntityHopper.registerFixesHopper(datafixer);
        BlockJukebox.registerFixesJukebox(datafixer);
        TileEntityMobSpawner.registerFixesMobSpawner(datafixer);
        registerFixes(datafixer);
        return datafixer;
    }

    public static NBTTagCompound processItemStack(IDataFixer fixer, NBTTagCompound compound, int version, String key)
    {
        if (compound.hasKey(key, 10))
        {
            compound.setTag(key, fixer.process(FixTypes.ITEM_INSTANCE, compound.getCompoundTag(key), version));
        }

        return compound;
    }

    public static NBTTagCompound processInventory(IDataFixer fixer, NBTTagCompound compound, int version, String key)
    {
        if (compound.hasKey(key, 9))
        {
            NBTTagList nbttaglist = compound.getTagList(key, 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                nbttaglist.set(i, fixer.process(FixTypes.ITEM_INSTANCE, nbttaglist.getCompoundTagAt(i), version));
            }
        }

        return compound;
    }
}