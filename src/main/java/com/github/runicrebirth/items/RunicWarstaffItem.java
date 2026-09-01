package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.client.animations.WarstaffAnimLayer;
import com.github.runicrebirth.network.RunicWeaponAnimS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunicWarstaffItem extends SwordItem implements GeoItem, IMagicWeapon {

    public static final ResourceLocation COOLDOWN_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_warstaff");
    public static final int COOLDOWN_TICKS = 100;
    private static final ResourceLocation RANGE_MOD_ID =
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "runic_warstaff_range");

    private static final Tier TIER = new Tier() {
        public int getUses() { return 400; }
        public float getSpeed() { return 8.0f; }
        public float getAttackDamageBonus() { return 5.0f; }
        public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }
        public int getEnchantmentValue() { return 10; }
        public Ingredient getRepairIngredient() { return Ingredient.of(net.minecraft.world.item.Items.DIAMOND); }
    };

    private static final RawAnimation ANIM_IDLE     = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_SPINNING = RawAnimation.begin().thenLoop("spinning");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunicWarstaffItem(Properties properties) {
        super(TIER, properties.stacksTo(1));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        var builder = ItemAttributeModifiers.builder();
        for (var entry : super.getDefaultAttributeModifiers().modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }
        builder.add(Attributes.ENTITY_INTERACTION_RANGE,
            new AttributeModifier(RANGE_MOD_ID, 1.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @Override
    public void activate(ServerPlayer sp) {
        MagicData data = MagicData.of(sp);
        if (data.isOnCooldown(COOLDOWN_ID)) return;
        data.startWhirlwind(10.0f);
        data.startCooldown(COOLDOWN_ID, COOLDOWN_TICKS);
        RunicWeaponAnimS2CPacket.send(sp, RunicWeaponAnimS2CPacket.Anim.WARSTAFF_SPIN);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public ResourceLocation getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, state -> {
            ItemDisplayContext ctx = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
            boolean isHand = ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            if (!isHand) {
                state.setAnimation(ANIM_IDLE);
                return PlayState.CONTINUE;
            }
            Entity raw = state.getData(DataTickets.ENTITY);
            LivingEntity holder = raw instanceof LivingEntity le ? le : Minecraft.getInstance().player;
            if (holder == null) {
                state.setAnimation(ANIM_IDLE);
                return PlayState.CONTINUE;
            }
            if (WarstaffAnimLayer.isSpinning(holder.getId())) {
                state.setAnimation(ANIM_SPINNING);
            } else {
                state.setAnimation(ANIM_IDLE);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public boolean isPerspectiveAware() { return true; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.github.runicrebirth.client.renderers.items.RunicWarstaffRenderer renderer;

            @Override
            @OnlyIn(Dist.CLIENT)
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.items.RunicWarstaffRenderer();
                }
                return renderer;
            }
        });
    }
}
