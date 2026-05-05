package com.github.interactivemagic.init;

import com.github.interactivemagic.InteractiveMagic;
import com.github.interactivemagic.entities.spells.MagicArrowEntity;
import com.github.interactivemagic.entities.spells.MagicBallistaEntity;
import com.github.interactivemagic.entities.spells.MagicBindingEntity;
import com.github.interactivemagic.entities.spells.MagicHammerEntity;
import com.github.interactivemagic.entities.spells.BasicCircleEntity;
import com.github.interactivemagic.entities.spells.MagicMeteorEntity;
import com.github.interactivemagic.entities.spells.MagicProjectileEntity;
import com.github.interactivemagic.entities.spells.MagicShieldEntity;
import com.github.interactivemagic.entities.spells.MagicSlashEntity;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = InteractiveMagic.MODID)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
        Registries.ENTITY_TYPE, InteractiveMagic.MODID);


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
            .sized(0.3F, 0.3F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_arrow"));

    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlashEntity>> MAGIC_SLASH = ENTITIES.register(
        "magic_slash",
        () -> EntityType.Builder.<MagicSlashEntity>of(MagicSlashEntity::new, MobCategory.MISC)
            .sized(3.0F, 1.0F)
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
            .sized(0.8F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(10)
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
            .sized(2.0F, 2.0F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("magic_ballista"));

    public static final DeferredHolder<EntityType<?>, EntityType<BasicCircleEntity>> BASIC_CIRCLE = ENTITIES.register(
        "basic_circle",
        () -> EntityType.Builder.<BasicCircleEntity>of(BasicCircleEntity::new, MobCategory.MISC)
            .sized(0.7F, 0.7F)
            .clientTrackingRange(8)
            .updateInterval(5)
            .noSave()
            .build("basic_circle"));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(
        String key, EntityType.EntityFactory<T> factory, MobCategory category, Function<Builder<T>, Builder<T>> builder) {
        return ENTITIES.register(key, () -> builder.apply(EntityType.Builder.of(factory, category)).build(key));
    }

}
