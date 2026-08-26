package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellParams;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.ThrownRunicDaggerEntity;
import com.github.runicrebirth.entities.spells.MagicExplosionEntity;
import com.github.runicrebirth.init.ModEntities;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.magic.stack.SpellResolver;
import com.github.runicrebirth.network.DaggerAnimS2CPacket;
import com.github.runicrebirth.network.RunicWeaponAnimS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunicDaggerItem extends SwordItem implements GeoItem, IMagicWeapon {

    public static final ResourceLocation COOLDOWN_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_dagger");
    public static final int COOLDOWN_TICKS = 60;
    private static final ResourceLocation RANGE_MOD_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_dagger_range");

    private static final Tier TIER = new Tier() {
        public int getUses() { return 250; }
        public float getSpeed() { return 6.0f; }
        public float getAttackDamageBonus() { return 3.0f; }
        public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }
        public int getEnchantmentValue() { return 16; }
        public Ingredient getRepairIngredient() { return Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT); }
    };

    private static final RawAnimation ANIM_IDLE      = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_THROWN    = RawAnimation.begin().thenLoop("thrown");
    private static final RawAnimation ANIM_RETURNING = RawAnimation.begin().thenLoop("returning");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunicDaggerItem(Properties properties) {
        super(TIER, properties.stacksTo(1));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        var builder = ItemAttributeModifiers.builder();
        for (var entry : super.getDefaultAttributeModifiers().modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }
        builder.add(Attributes.ENTITY_INTERACTION_RANGE,
            new AttributeModifier(RANGE_MOD_ID, -1.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @Override
    public void activate(ServerPlayer sp) {
        MagicData data = MagicData.of(sp);
        int dagId = data.thrownDaggerEntityId();
        if (dagId == -1) return;
        if (data.isOnCooldown(COOLDOWN_ID)) return;

        Entity dagEntity = sp.serverLevel().getEntity(dagId);
        if (dagEntity instanceof ThrownRunicDaggerEntity dagger) {
            Vec3 pos = dagger.position();
            Vec3 dir = sp.getLookAngle().normalize();
            SpellCastContext ctx = new SpellCastContext(
                sp.serverLevel(), sp, sp.getMainHandItem(), pos, dir, sp.getXRot(), sp.getYRot(), null);
            SpellStack tmp = new SpellStack();
            tmp.append(ModSpellTypes.MAGIC_EXPLOSION.get());
            SpellParams params = SpellResolver.buildParams(ctx, tmp);
            if (params != null) {
                com.github.runicrebirth.api.spells.Element runeElement =
                    com.github.runicrebirth.rune.RuneEffectApplicator.getActiveElement(sp.getMainHandItem());
                if (runeElement != null) params.element = runeElement;
                MagicExplosionEntity explosion = new MagicExplosionEntity(sp.level(), sp, params);
                explosion.setPos(pos.x, pos.y, pos.z);
                sp.level().addFreshEntity(explosion);
            }
            dagger.discard();
        }
        data.clearThrownDaggerEntityId();
        data.startCooldown(COOLDOWN_ID, COOLDOWN_TICKS);
        DaggerAnimS2CPacket.send(sp, DaggerAnimS2CPacket.Anim.IDLE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.consume(stack);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.pass(stack);

        MagicData data = MagicData.of(sp);

        if (data.thrownDaggerEntityId() != -1) {
            Entity dagEntity = sp.serverLevel().getEntity(data.thrownDaggerEntityId());
            if (dagEntity instanceof ThrownRunicDaggerEntity dagger) {
                dagger.setPhase(ThrownRunicDaggerEntity.Phase.RETURNING);
                DaggerAnimS2CPacket.send(sp, DaggerAnimS2CPacket.Anim.RETURNING);
            } else {
                data.clearThrownDaggerEntityId();
            }
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        if (data.isOnCooldown(COOLDOWN_ID)) return InteractionResultHolder.pass(stack);

        Vec3 dir = sp.getLookAngle().normalize();
        double yawRad = Math.toRadians(sp.getYRot());
        Vec3 rightDir = new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
        Vec3 spawnPos = sp.getEyePosition()
            .add(rightDir.scale(-0.35))
            .add(0, -0.35, 0);

        ThrownRunicDaggerEntity dagger = new ThrownRunicDaggerEntity(ModEntities.THROWN_RUNIC_DAGGER.get(), level);
        dagger.setOwner(sp);
        dagger.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        dagger.setDeltaMovement(dir.scale(2.5));
        float spawnYaw = (float) Math.toDegrees(Math.atan2(dir.x, -dir.z));
        float spawnPitch = (float) Math.toDegrees(Math.atan2(-dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)));
        dagger.setYRot(spawnYaw);
        dagger.setXRot(spawnPitch);
        level.addFreshEntity(dagger);
        data.setThrownDaggerEntityId(dagger.getId());

        DaggerAnimS2CPacket.send(sp, DaggerAnimS2CPacket.Anim.THROWN);
        RunicWeaponAnimS2CPacket.send(sp, RunicWeaponAnimS2CPacket.Anim.DAGGER_THROW);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public ResourceLocation getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5,
            state -> { state.setAnimation(ANIM_IDLE); return PlayState.CONTINUE; }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.items.RunicDaggerRenderer renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.items.RunicDaggerRenderer();
                }
                return renderer;
            }
        });
    }
}
