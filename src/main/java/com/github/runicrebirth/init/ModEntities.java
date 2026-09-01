package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.entities.ArcaneDroneEntity;
import com.github.runicrebirth.entities.SeatEntity;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import com.github.runicrebirth.entities.CrumblingPlatformFallingEntity;
import com.github.runicrebirth.entities.DungeonBoulderEntity;
import com.github.runicrebirth.entities.DrawingCanvasEntity;
import com.github.runicrebirth.entities.HammerDroneEntity;
import com.github.runicrebirth.entities.MagicHandEntity;
import com.github.runicrebirth.entities.PhantomMinerEntity;
import com.github.runicrebirth.entities.mobs.AncientArcaneDroneEntity;
import com.github.runicrebirth.entities.mobs.RunesteelGolemEntity;
import com.github.runicrebirth.entities.mobs.SkeletalMageAcolyteEntity;
import com.github.runicrebirth.entities.mobs.SkeletalWizardAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedArtificerAcolyteEntity;
import com.github.runicrebirth.entities.mobs.ZombifiedRunebladeAcolyteEntity;
import com.github.runicrebirth.entities.spells.EarthQuicksandEntity;
import com.github.runicrebirth.entities.spells.FrozenEffectEntity;
import com.github.runicrebirth.entities.spells.ArcaneTetherEntity;
import com.github.runicrebirth.entities.spells.AoeTrackerEntity;
import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.entities.spells.TargetCircleEntity;
import com.github.runicrebirth.entities.spells.MagicArrowEntity;
import com.github.runicrebirth.entities.spells.MagicBallistaCircleEntity;
import com.github.runicrebirth.entities.spells.MagicBallistaEntity;
import com.github.runicrebirth.entities.spells.MagicBeamEntity;
import com.github.runicrebirth.entities.spells.MagicBlastEntity;
import com.github.runicrebirth.entities.spells.MagicBindingEntity;
import com.github.runicrebirth.entities.spells.InfusionCircleEntity;
import com.github.runicrebirth.entities.spells.MagicExplosionEntity;
import com.github.runicrebirth.entities.spells.MagicHammerEntity;
import com.github.runicrebirth.entities.spells.AdvancedCircleEntity;
import com.github.runicrebirth.entities.spells.BasicCircleEntity;
import com.github.runicrebirth.entities.spells.IntermediateCircleEntity;
import com.github.runicrebirth.entities.spells.MagicMeteorCircleEntity;
import com.github.runicrebirth.entities.spells.MagicMeteorEntity;
import com.github.runicrebirth.entities.spells.MagicProjectileEntity;
import com.github.runicrebirth.entities.spells.MagicShieldEntity;
import com.github.runicrebirth.entities.spells.MagicSlashCircleEntity;
import com.github.runicrebirth.entities.spells.MagicSlashEntity;
import com.github.runicrebirth.entities.spells.demo.SpellDemoEntity;
import com.github.runicrebirth.entities.EarthVeinCircleEntity;
import com.github.runicrebirth.entities.EarthVeinRunesEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(RunicRebirth.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MagicProjectileEntity>> MAGIC_PROJECTILE =
        ENTITIES.registerEntityType("magic_projectile", MagicProjectileEntity::new, MobCategory.MISC,
            b -> b.sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicArrowEntity>> MAGIC_ARROW =
        ENTITIES.registerEntityType("magic_arrow", MagicArrowEntity::new, MobCategory.MISC,
            b -> b.sized(0.25F, 0.125F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlashEntity>> MAGIC_SLASH =
        ENTITIES.registerEntityType("magic_slash", MagicSlashEntity::new, MobCategory.MISC,
            b -> b.sized(2.25F, 0.125F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMeteorEntity>> MAGIC_METEOR =
        ENTITIES.registerEntityType("magic_meteor", MagicMeteorEntity::new, MobCategory.MISC,
            b -> b.sized(0.8F, 0.8F).clientTrackingRange(8).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicShieldEntity>> MAGIC_SHIELD =
        ENTITIES.registerEntityType("magic_shield", MagicShieldEntity::new, MobCategory.MISC,
            b -> b.sized(2.5F, 2.5F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicHammerEntity>> MAGIC_HAMMER =
        ENTITIES.registerEntityType("magic_hammer", MagicHammerEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 2.0F).clientTrackingRange(8).updateInterval(5));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBindingEntity>> MAGIC_BINDING =
        ENTITIES.registerEntityType("magic_binding", MagicBindingEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 2.0F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBallistaEntity>> MAGIC_BALLISTA =
        ENTITIES.registerEntityType("magic_ballista", MagicBallistaEntity::new, MobCategory.MISC,
            b -> b.sized(1.5F, 0.4375F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBlastEntity>> MAGIC_BLAST =
        ENTITIES.registerEntityType("magic_blast", MagicBlastEntity::new, MobCategory.MISC,
            b -> b.sized(2.0F, 2.0F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBeamEntity>> MAGIC_BEAM =
        ENTITIES.registerEntityType("magic_beam", MagicBeamEntity::new, MobCategory.MISC,
            b -> b.sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<BasicCircleEntity>> BASIC_CIRCLE =
        ENTITIES.registerEntityType("basic_circle", BasicCircleEntity::new, MobCategory.MISC,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<IntermediateCircleEntity>> INTERMEDIATE_CIRCLE =
        ENTITIES.registerEntityType("intermediate_circle", IntermediateCircleEntity::new, MobCategory.MISC,
            b -> b.sized(0.6F, 0.6F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedCircleEntity>> ADVANCED_CIRCLE =
        ENTITIES.registerEntityType("advanced_circle", AdvancedCircleEntity::new, MobCategory.MISC,
            b -> b.sized(0.8F, 0.8F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBallistaCircleEntity>> MAGIC_BALLISTA_CIRCLE =
        ENTITIES.registerEntityType("magic_ballista_circle", MagicBallistaCircleEntity::new, MobCategory.MISC,
            b -> b.sized(1.5F, 0.4375F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlashCircleEntity>> MAGIC_SLASH_CIRCLE =
        ENTITIES.registerEntityType("magic_slash_circle", MagicSlashCircleEntity::new, MobCategory.MISC,
            b -> b.sized(0.7F, 0.7F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMeteorCircleEntity>> MAGIC_METEOR_CIRCLE =
        ENTITIES.registerEntityType("magic_meteor_circle", MagicMeteorCircleEntity::new, MobCategory.MISC,
            b -> b.sized(2F, 2F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<InfusionCircleEntity>> INFUSION_CIRCLE =
        ENTITIES.registerEntityType("infusion_circle", InfusionCircleEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 1.0F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicExplosionEntity>> MAGIC_EXPLOSION =
        ENTITIES.registerEntityType("magic_explosion", MagicExplosionEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 1.0F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_SLASH_DEMO =
        ENTITIES.registerEntityType("magic_slash_demo", SpellDemoEntity::new, MobCategory.MISC,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(0).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_METEOR_DEMO =
        ENTITIES.registerEntityType("magic_meteor_demo", SpellDemoEntity::new, MobCategory.MISC,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(0).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_BALLISTA_DEMO =
        ENTITIES.registerEntityType("magic_ballista_demo", SpellDemoEntity::new, MobCategory.MISC,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(0).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<FrozenEffectEntity>> FROZEN_EFFECT =
        ENTITIES.registerEntityType("frozen_effect", FrozenEffectEntity::new, MobCategory.MISC,
            b -> b.sized(0.8F, 1.8F).clientTrackingRange(8).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<EarthQuicksandEntity>> EARTH_QUICKSAND =
        ENTITIES.registerEntityType("earth_quicksand", EarthQuicksandEntity::new, MobCategory.MISC,
            b -> b.sized(4.0F, 0.5F).clientTrackingRange(8).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneTetherEntity>> ARCANE_TETHER =
        ENTITIES.registerEntityType("arcane_tether", ArcaneTetherEntity::new, MobCategory.MISC,
            b -> b.sized(0.1F, 0.1F).clientTrackingRange(32).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<EnergyCracklingEntity>> ENERGY_CRACKLING =
        ENTITIES.registerEntityType("energy_crackling", EnergyCracklingEntity::new, MobCategory.MISC,
            b -> b.sized(0.1F, 0.1F).clientTrackingRange(8).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<DrawingCanvasEntity>> DRAWING_CANVAS =
        ENTITIES.registerEntityType("drawing_canvas", DrawingCanvasEntity::new, MobCategory.MISC,
            b -> b.sized(0.1F, 0.1F).clientTrackingRange(16).updateInterval(5).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<MagicHandEntity>> MAGIC_HAND =
        ENTITIES.registerEntityType("magic_hand", MagicHandEntity::new, MobCategory.MISC,
            b -> b.sized(0.8F, 0.8F).clientTrackingRange(8).updateInterval(2).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<PhantomMinerEntity>> PHANTOM_MINER =
        ENTITIES.registerEntityType("phantom_miner", PhantomMinerEntity::new, MobCategory.MISC,
            b -> b.sized(0.1F, 0.1F).clientTrackingRange(8).updateInterval(2).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<CrumblingPlatformFallingEntity>> CRUMBLING_PLATFORM_FALLING =
        ENTITIES.registerEntityType("crumbling_platform_falling", CrumblingPlatformFallingEntity::new, MobCategory.MISC,
            b -> b.sized(0.98F, 0.98F).clientTrackingRange(4).updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<DungeonBoulderEntity>> DUNGEON_BOULDER =
        ENTITIES.registerEntityType("dungeon_boulder", DungeonBoulderEntity::new, MobCategory.MISC,
            b -> b.sized(2.0F, 2.0F).clientTrackingRange(10).updateInterval(1));

    public static final DeferredHolder<EntityType<?>, EntityType<TargetCircleEntity>> TARGET_CIRCLE =
        ENTITIES.registerEntityType("target_circle", TargetCircleEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 0.1F).clientTrackingRange(0).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<AoeTrackerEntity>> AOE_TRACKER =
        ENTITIES.registerEntityType("aoe_tracker", AoeTrackerEntity::new, MobCategory.MISC,
            b -> b.sized(1.0F, 0.1F).clientTrackingRange(0).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRunicDaggerEntity>> THROWN_RUNIC_DAGGER =
        ENTITIES.registerEntityType("thrown_runic_dagger", ThrownRunicDaggerEntity::new, MobCategory.MISC,
            b -> b.sized(0.2F, 0.3F).clientTrackingRange(6).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneDroneEntity>> ARCANE_DRONE =
        ENTITIES.registerEntityType("arcane_drone", ArcaneDroneEntity::new, MobCategory.MISC,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT =
        ENTITIES.registerEntityType("seat", SeatEntity::new, MobCategory.MISC,
            b -> b.sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(40).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<HammerDroneEntity>> HAMMER_DRONE =
        ENTITIES.registerEntityType("hammer_drone", HammerDroneEntity::new, MobCategory.MISC,
            b -> b.sized(0.6F, 0.6F).clientTrackingRange(8).updateInterval(3).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<AncientArcaneDroneEntity>> ANCIENT_ARCANE_DRONE =
        ENTITIES.registerEntityType("ancient_arcane_drone", AncientArcaneDroneEntity::new, MobCategory.MONSTER,
            b -> b.sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<RunesteelGolemEntity>> RUNESTEEL_GOLEM =
        ENTITIES.registerEntityType("runesteel_golem", RunesteelGolemEntity::new, MobCategory.MONSTER,
            b -> b.sized(2F, 3F).clientTrackingRange(10).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombifiedRunebladeAcolyteEntity>> ZOMBIFIED_RUNEBLADE_ACOLYTE =
        ENTITIES.registerEntityType("zombified_runeblade_acolyte", ZombifiedRunebladeAcolyteEntity::new, MobCategory.MONSTER,
            b -> b.sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalMageAcolyteEntity>> SKELETAL_MAGE_ACOLYTE =
        ENTITIES.registerEntityType("skeletal_mage_acolyte", SkeletalMageAcolyteEntity::new, MobCategory.MONSTER,
            b -> b.sized(0.6F, 1.99F).clientTrackingRange(10).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalWizardAcolyteEntity>> SKELETAL_WIZARD_ACOLYTE =
        ENTITIES.registerEntityType("skeletal_wizard_acolyte", SkeletalWizardAcolyteEntity::new, MobCategory.MONSTER,
            b -> b.sized(0.6F, 1.99F).clientTrackingRange(10).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombifiedArtificerAcolyteEntity>> ZOMBIFIED_ARTIFICER_ACOLYTE =
        ENTITIES.registerEntityType("zombified_artificer_acolyte", ZombifiedArtificerAcolyteEntity::new, MobCategory.MONSTER,
            b -> b.sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3));

    public static final DeferredHolder<EntityType<?>, EntityType<EarthVeinRunesEntity>> EARTH_VEIN_RUNES =
        ENTITIES.registerEntityType("earth_vein_runes", EarthVeinRunesEntity::new, MobCategory.MISC,
            b -> b.sized(8.0F, 5.5F).clientTrackingRange(16).updateInterval(20).noSave());

    public static final DeferredHolder<EntityType<?>, EntityType<EarthVeinCircleEntity>> EARTH_VEIN_CIRCLE =
        ENTITIES.registerEntityType("earth_vein_circle", EarthVeinCircleEntity::new, MobCategory.MISC,
            b -> b.sized(3.0F, 0.2F).clientTrackingRange(16).updateInterval(5).noSave());

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ANCIENT_ARCANE_DRONE.get(), AncientArcaneDroneEntity.createAttributes().build());
        event.put(RUNESTEEL_GOLEM.get(), RunesteelGolemEntity.createAttributes().build());
        event.put(ZOMBIFIED_RUNEBLADE_ACOLYTE.get(), ZombifiedRunebladeAcolyteEntity.createAttributes().build());
        event.put(SKELETAL_MAGE_ACOLYTE.get(), SkeletalMageAcolyteEntity.createAttributes().build());
        event.put(SKELETAL_WIZARD_ACOLYTE.get(), SkeletalWizardAcolyteEntity.createAttributes().build());
        event.put(ZOMBIFIED_ARTIFICER_ACOLYTE.get(), ZombifiedArtificerAcolyteEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ANCIENT_ARCANE_DRONE.get(), SpawnPlacementTypes.NO_RESTRICTIONS,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
        event.register(RUNESTEEL_GOLEM.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
        event.register(ZOMBIFIED_RUNEBLADE_ACOLYTE.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
        event.register(SKELETAL_MAGE_ACOLYTE.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
        event.register(SKELETAL_WIZARD_ACOLYTE.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
        event.register(ZOMBIFIED_ARTIFICER_ACOLYTE.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.OR);
    }
}
