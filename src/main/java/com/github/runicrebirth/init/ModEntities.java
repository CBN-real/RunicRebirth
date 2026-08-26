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
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = RunicRebirth.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
        Registries.ENTITY_TYPE, RunicRebirth.MODID);


    public static final DeferredHolder<EntityType<?>, EntityType<MagicProjectileEntity>> MAGIC_PROJECTILE = ENTITIES.register(
        "magic_projectile",
        () -> EntityType.Builder.<MagicProjectileEntity>of(MagicProjectileEntity::new, MobCategory.MISC)
            .sized(0.3F, 0.3F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicArrowEntity>> MAGIC_ARROW = ENTITIES.register(
        "magic_arrow",
        () -> EntityType.Builder.<MagicArrowEntity>of(MagicArrowEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.125F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_arrow"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlashEntity>> MAGIC_SLASH = ENTITIES.register(
        "magic_slash",
        () -> EntityType.Builder.<MagicSlashEntity>of(MagicSlashEntity::new, MobCategory.MISC)
            .sized(2.25F, 0.125F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_slash"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMeteorEntity>> MAGIC_METEOR = ENTITIES.register(
        "magic_meteor",
        () -> EntityType.Builder.<MagicMeteorEntity>of(MagicMeteorEntity::new, MobCategory.MISC)
            .sized(0.8F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(10)
            .build("magic_meteor"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicShieldEntity>> MAGIC_SHIELD = ENTITIES.register(
        "magic_shield",
        () -> EntityType.Builder.<MagicShieldEntity>of(MagicShieldEntity::new, MobCategory.MISC)
            .sized(2.5F, 2.5F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_shield"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicHammerEntity>> MAGIC_HAMMER = ENTITIES.register(
        "magic_hammer",
        () -> EntityType.Builder.<MagicHammerEntity>of(MagicHammerEntity::new, MobCategory.MISC)
            .sized(1.0F, 2.0F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .build("magic_hammer"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBindingEntity>> MAGIC_BINDING = ENTITIES.register(
        "magic_binding",
        () -> EntityType.Builder.<MagicBindingEntity>of(MagicBindingEntity::new, MobCategory.MISC)
            .sized(1.0F, 2.0F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_binding"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBallistaEntity>> MAGIC_BALLISTA = ENTITIES.register(
        "magic_ballista",
        () -> EntityType.Builder.<MagicBallistaEntity>of(MagicBallistaEntity::new, MobCategory.MISC)
            .sized(1.5F, 0.4375F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_ballista"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBlastEntity>> MAGIC_BLAST = ENTITIES.register(
        "magic_blast",
        () -> EntityType.Builder.<MagicBlastEntity>of(MagicBlastEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_blast"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBeamEntity>> MAGIC_BEAM = ENTITIES.register(
        "magic_beam",
        () -> EntityType.Builder.<MagicBeamEntity>of(MagicBeamEntity::new, MobCategory.MISC)
            .sized(0.3F, 0.3F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .noSave()
            .build("magic_beam"));

    public static final DeferredHolder<EntityType<?>, EntityType<BasicCircleEntity>> BASIC_CIRCLE = ENTITIES.register(
        "basic_circle",
        () -> EntityType.Builder.<BasicCircleEntity>of(BasicCircleEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("basic_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<IntermediateCircleEntity>> INTERMEDIATE_CIRCLE = ENTITIES.register(
        "intermediate_circle",
        () -> EntityType.Builder.<IntermediateCircleEntity>of(IntermediateCircleEntity::new, MobCategory.MISC)
            .sized(0.6F, 0.6F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("intermediate_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedCircleEntity>> ADVANCED_CIRCLE = ENTITIES.register(
        "advanced_circle",
        () -> EntityType.Builder.<AdvancedCircleEntity>of(AdvancedCircleEntity::new, MobCategory.MISC)
            .sized(0.8F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("advanced_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicBallistaCircleEntity>> MAGIC_BALLISTA_CIRCLE = ENTITIES.register(
        "magic_ballista_circle",
        () -> EntityType.Builder.<MagicBallistaCircleEntity>of(MagicBallistaCircleEntity::new, MobCategory.MISC)
            .sized(1.5F, 0.4375F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_ballista_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlashCircleEntity>> MAGIC_SLASH_CIRCLE = ENTITIES.register(
        "magic_slash_circle",
        () -> EntityType.Builder.<MagicSlashCircleEntity>of(MagicSlashCircleEntity::new, MobCategory.MISC)
            .sized(0.7F, 0.7F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_slash_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMeteorCircleEntity>> MAGIC_METEOR_CIRCLE = ENTITIES.register(
        "magic_meteor_circle",
        () -> EntityType.Builder.<MagicMeteorCircleEntity>of(MagicMeteorCircleEntity::new, MobCategory.MISC)
            .sized(2F, 2F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_meteor_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<InfusionCircleEntity>> INFUSION_CIRCLE = ENTITIES.register(
        "infusion_circle",
        () -> EntityType.Builder.<InfusionCircleEntity>of(InfusionCircleEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("infusion_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicExplosionEntity>> MAGIC_EXPLOSION = ENTITIES.register(
        "magic_explosion",
        () -> EntityType.Builder.<MagicExplosionEntity>of(MagicExplosionEntity::new, MobCategory.MISC)
            .sized(1.0F, 1.0F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("magic_explosion"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_SLASH_DEMO = ENTITIES.register(
        "magic_slash_demo",
        () -> EntityType.Builder.<SpellDemoEntity>of(SpellDemoEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(0)
            .noSave()
            .build("magic_slash_demo"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_METEOR_DEMO = ENTITIES.register(
        "magic_meteor_demo",
        () -> EntityType.Builder.<SpellDemoEntity>of(SpellDemoEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(0)
            .noSave()
            .build("magic_meteor_demo"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpellDemoEntity>> MAGIC_BALLISTA_DEMO = ENTITIES.register(
        "magic_ballista_demo",
        () -> EntityType.Builder.<SpellDemoEntity>of(SpellDemoEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(0)
            .noSave()
            .build("magic_ballista_demo"));

    public static final DeferredHolder<EntityType<?>, EntityType<FrozenEffectEntity>> FROZEN_EFFECT = ENTITIES.register(
        "frozen_effect",
        () -> EntityType.Builder.<FrozenEffectEntity>of(FrozenEffectEntity::new, MobCategory.MISC)
            .sized(0.8F, 1.8F).clientTrackingRange(8).updateInterval(3).noSave()
            .build("frozen_effect"));

    public static final DeferredHolder<EntityType<?>, EntityType<EarthQuicksandEntity>> EARTH_QUICKSAND = ENTITIES.register(
        "earth_quicksand",
        () -> EntityType.Builder.<EarthQuicksandEntity>of(EarthQuicksandEntity::new, MobCategory.MISC)
            .sized(4.0F, 0.5F).clientTrackingRange(8).updateInterval(3).noSave()
            .build("earth_quicksand"));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneTetherEntity>> ARCANE_TETHER = ENTITIES.register(
        "arcane_tether",
        () -> EntityType.Builder.<ArcaneTetherEntity>of(ArcaneTetherEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(32)
            .updateInterval(3)
            .noSave()
            .build("arcane_tether"));

    public static final DeferredHolder<EntityType<?>, EntityType<EnergyCracklingEntity>> ENERGY_CRACKLING = ENTITIES.register(
        "energy_crackling",
        () -> EntityType.Builder.<EnergyCracklingEntity>of(EnergyCracklingEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("energy_crackling"));

    public static final DeferredHolder<EntityType<?>, EntityType<DrawingCanvasEntity>> DRAWING_CANVAS = ENTITIES.register(
        "drawing_canvas",
        () -> EntityType.Builder.<DrawingCanvasEntity>of(DrawingCanvasEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(16)
            .updateInterval(5)
            .noSave()
            .build("drawing_canvas"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicHandEntity>> MAGIC_HAND = ENTITIES.register(
        "magic_hand",
        () -> EntityType.Builder.<MagicHandEntity>of(MagicHandEntity::new, MobCategory.MISC)
            .sized(0.8F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(2)
            .noSave()
            .build("magic_hand"));

    public static final DeferredHolder<EntityType<?>, EntityType<PhantomMinerEntity>> PHANTOM_MINER = ENTITIES.register(
        "phantom_miner",
        () -> EntityType.Builder.<PhantomMinerEntity>of(PhantomMinerEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(2)
            .noSave()
            .build("phantom_miner"));

    public static final DeferredHolder<EntityType<?>, EntityType<CrumblingPlatformFallingEntity>> CRUMBLING_PLATFORM_FALLING = ENTITIES.register(
        "crumbling_platform_falling",
        () -> EntityType.Builder.<CrumblingPlatformFallingEntity>of(CrumblingPlatformFallingEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.98F)
            .clientTrackingRange(4)
            .updateInterval(1)
            .build("crumbling_platform_falling"));

    public static final DeferredHolder<EntityType<?>, EntityType<DungeonBoulderEntity>> DUNGEON_BOULDER = ENTITIES.register(
        "dungeon_boulder",
        () -> EntityType.Builder.<DungeonBoulderEntity>of(DungeonBoulderEntity::new, MobCategory.MISC)
            .sized(2.0F, 2.0F)
            .clientTrackingRange(10)
            .updateInterval(1)
            .build("dungeon_boulder"));

    public static final DeferredHolder<EntityType<?>, EntityType<TargetCircleEntity>> TARGET_CIRCLE = ENTITIES.register(
        "target_circle",
        () -> EntityType.Builder.<TargetCircleEntity>of(TargetCircleEntity::new, MobCategory.MISC)
            .sized(1.0F, 0.1F)
            .clientTrackingRange(0)
            .noSave()
            .build("target_circle"));

    public static final DeferredHolder<EntityType<?>, EntityType<AoeTrackerEntity>> AOE_TRACKER = ENTITIES.register(
        "aoe_tracker",
        () -> EntityType.Builder.<AoeTrackerEntity>of(AoeTrackerEntity::new, MobCategory.MISC)
            .sized(1.0F, 0.1F)
            .clientTrackingRange(0)
            .noSave()
            .build("aoe_tracker"));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownRunicDaggerEntity>> THROWN_RUNIC_DAGGER =
        ENTITIES.register("thrown_runic_dagger",
            () -> EntityType.Builder.<ThrownRunicDaggerEntity>of(ThrownRunicDaggerEntity::new, MobCategory.MISC)
                .sized(0.2F, 0.3F)
                .clientTrackingRange(6)
                .updateInterval(3)
                .build("thrown_runic_dagger"));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneDroneEntity>> ARCANE_DRONE = ENTITIES.register(
        "arcane_drone",
        () -> EntityType.Builder.<ArcaneDroneEntity>of(ArcaneDroneEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .noSave()
            .build("arcane_drone"));

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = ENTITIES.register(
        "seat",
        () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
            .sized(0.0F, 0.0F)
            .clientTrackingRange(10)
            .updateInterval(40)
            .noSave()
            .build("seat"));

    public static final DeferredHolder<EntityType<?>, EntityType<HammerDroneEntity>> HAMMER_DRONE = ENTITIES.register(
        "hammer_drone",
        () -> EntityType.Builder.<HammerDroneEntity>of(HammerDroneEntity::new, MobCategory.MISC)
            .sized(0.6F, 0.6F)
            .clientTrackingRange(8)
            .updateInterval(3)
            .noSave()
            .build("hammer_drone"));

    public static final DeferredHolder<EntityType<?>, EntityType<AncientArcaneDroneEntity>> ANCIENT_ARCANE_DRONE = ENTITIES.register(
        "ancient_arcane_drone",
        () -> EntityType.Builder.<AncientArcaneDroneEntity>of(AncientArcaneDroneEntity::new, MobCategory.MONSTER)
            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(3).build("ancient_arcane_drone"));

    public static final DeferredHolder<EntityType<?>, EntityType<RunesteelGolemEntity>> RUNESTEEL_GOLEM = ENTITIES.register(
        "runesteel_golem",
        () -> EntityType.Builder.<RunesteelGolemEntity>of(RunesteelGolemEntity::new, MobCategory.MONSTER)
            .sized(2F, 3F).clientTrackingRange(10).updateInterval(3).build("runesteel_golem"));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombifiedRunebladeAcolyteEntity>> ZOMBIFIED_RUNEBLADE_ACOLYTE = ENTITIES.register(
        "zombified_runeblade_acolyte",
        () -> EntityType.Builder.<ZombifiedRunebladeAcolyteEntity>of(ZombifiedRunebladeAcolyteEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3).build("zombified_runeblade_acolyte"));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalMageAcolyteEntity>> SKELETAL_MAGE_ACOLYTE = ENTITIES.register(
        "skeletal_mage_acolyte",
        () -> EntityType.Builder.<SkeletalMageAcolyteEntity>of(SkeletalMageAcolyteEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(10).updateInterval(3).build("skeletal_mage_acolyte"));

    public static final DeferredHolder<EntityType<?>, EntityType<SkeletalWizardAcolyteEntity>> SKELETAL_WIZARD_ACOLYTE = ENTITIES.register(
        "skeletal_wizard_acolyte",
        () -> EntityType.Builder.<SkeletalWizardAcolyteEntity>of(SkeletalWizardAcolyteEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F).clientTrackingRange(10).updateInterval(3).build("skeletal_wizard_acolyte"));

    public static final DeferredHolder<EntityType<?>, EntityType<ZombifiedArtificerAcolyteEntity>> ZOMBIFIED_ARTIFICER_ACOLYTE = ENTITIES.register(
        "zombified_artificer_acolyte",
        () -> EntityType.Builder.<ZombifiedArtificerAcolyteEntity>of(ZombifiedArtificerAcolyteEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3).build("zombified_artificer_acolyte"));

    public static final DeferredHolder<EntityType<?>, EntityType<EarthVeinRunesEntity>> EARTH_VEIN_RUNES = ENTITIES.register(
        "earth_vein_runes",
        () -> EntityType.Builder.<EarthVeinRunesEntity>of(EarthVeinRunesEntity::new, MobCategory.MISC)
            .sized(8.0F, 5.5F)
            .clientTrackingRange(16)
            .updateInterval(20)
            .noSave()
            .build("earth_vein_runes"));

    public static final DeferredHolder<EntityType<?>, EntityType<EarthVeinCircleEntity>> EARTH_VEIN_CIRCLE = ENTITIES.register(
        "earth_vein_circle",
        () -> EntityType.Builder.<EarthVeinCircleEntity>of(EarthVeinCircleEntity::new, MobCategory.MISC)
            .sized(3.0F, 0.2F)
            .clientTrackingRange(16)
            .updateInterval(5)
            .noSave()
            .build("earth_vein_circle"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ANCIENT_ARCANE_DRONE.get(), AncientArcaneDroneEntity.createAttributes().build());
        event.put(RUNESTEEL_GOLEM.get(), RunesteelGolemEntity.createAttributes().build());
        event.put(ZOMBIFIED_RUNEBLADE_ACOLYTE.get(), ZombifiedRunebladeAcolyteEntity.createAttributes().build());
        event.put(SKELETAL_MAGE_ACOLYTE.get(), SkeletalMageAcolyteEntity.createAttributes().build());
        event.put(SKELETAL_WIZARD_ACOLYTE.get(), SkeletalWizardAcolyteEntity.createAttributes().build());
        event.put(ZOMBIFIED_ARTIFICER_ACOLYTE.get(), ZombifiedArtificerAcolyteEntity.createAttributes().build());
    }

    @SubscribeEvent
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

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(
        String key, EntityType.EntityFactory<T> factory, MobCategory category, Function<Builder<T>, Builder<T>> builder) {
        return ENTITIES.register(key, () -> builder.apply(EntityType.Builder.of(factory, category)).build(key));
    }

}
