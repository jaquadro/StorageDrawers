package thaumcraft.api.golems;

import net.minecraft.util.Identifier;
import net.minecraft.util.StatCollector;

public enum EnumGolemTrait {
	SMART(new Identifier("thaumcraft","textures/misc/golem/tag_smart.png")), 
	DEFT(new Identifier("thaumcraft","textures/misc/golem/tag_deft.png")), 
	CLUMSY(new Identifier("thaumcraft","textures/misc/golem/tag_clumsy.png")), 
	FIGHTER(new Identifier("thaumcraft","textures/misc/golem/tag_fighter.png")), 
	WHEELED(new Identifier("thaumcraft","textures/misc/golem/tag_wheeled.png")), 
	FLYER(new Identifier("thaumcraft","textures/misc/golem/tag_flyer.png")), 
	CLIMBER(new Identifier("thaumcraft","textures/misc/golem/tag_climber.png")),
	HEAVY(new Identifier("thaumcraft","textures/misc/golem/tag_heavy.png")),
	LIGHT(new Identifier("thaumcraft","textures/misc/golem/tag_light.png")),
	FRAGILE(new Identifier("thaumcraft","textures/misc/golem/tag_fragile.png")),
	REPAIR(new Identifier("thaumcraft","textures/misc/golem/tag_repair.png")), 
	SCOUT(new Identifier("thaumcraft","textures/misc/golem/tag_scout.png")), 
	ARMORED(new Identifier("thaumcraft","textures/misc/golem/tag_armored.png")), 
	BRUTAL(new Identifier("thaumcraft","textures/misc/golem/tag_brutal.png")),
	FIREPROOF(new Identifier("thaumcraft","textures/misc/golem/tag_fireproof.png")),
	BREAKER(new Identifier("thaumcraft","textures/misc/golem/tag_breaker.png")),
	HAULER(new Identifier("thaumcraft","textures/misc/golem/tag_hauler.png")),
	RANGED(new Identifier("thaumcraft","textures/misc/golem/tag_ranged.png"));
	
	static {
		CLUMSY.opposite = DEFT;
		DEFT.opposite = CLUMSY;
		
		HEAVY.opposite = LIGHT;
		LIGHT.opposite = HEAVY;
		
		FRAGILE.opposite = ARMORED;
		ARMORED.opposite = FRAGILE;
	}
	
	public Identifier icon;
	public EnumGolemTrait opposite;
	
	private EnumGolemTrait(Identifier icon) {
		this.icon = icon;
	}
	
	public String getLocalizedName() {
		return StatCollector.translateToLocal("golem.trait."+this.name().toLowerCase());
	}
	
	public String getLocalizedDescription() {
		return StatCollector.translateToLocal("golem.trait.text."+this.name().toLowerCase());
	}
}