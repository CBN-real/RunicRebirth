package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.client.animations.WarstaffAnimLayer;
import com.github.runicrebirth.network.RunicWeaponAnimS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import net.minecraft.world.item.ItemDisplayContext;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.constant.DataTickets;
import com.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class RunicWarstaffItem extends Item implements GeoItem, IMagicWeapon {

    public static final Identifier COOLDOWN_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_warstaff");
    public static final int COOLDOWN_TICKS = 100;
    private static final Identifier RANGE_MOD_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "runic_warstaff_range");

    private static final ToolMaterial TIER = new ToolMaterial(
        BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 400, 8.0f, 5.0f, 10, ItemTags.DIAMOND_TOOL_MATERIALS
    );

    private static final RawAnimation ANIM_IDLE     = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation ANIM_SPINNING = RawAnimation.begin().thenLoop("spinning");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RunicWarstaffItem(Item.Properties properties) {
        super(TIER.applySwordProperties(properties.stacksTo(1), 3.0f, -2.4f));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        for (var entry : super.getDefaultAttributeModifiers(stack).modifiers()) {
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public Identifier getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<RunicWarstaffItem>("controller", 5, state -> {
            ItemDisplayContext ctx = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
            boolean isHand = ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            if (!isHand) {
                state.setAnimation(ANIM_IDLE);
                return PlayState.CONTINUE;
            }
            LivingEntity holder = Minecraft.getInstance().player;
            if (holder != null && WarstaffAnimLayer.isSpinning(holder.getId())) {
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
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new com.github.runicrebirth.client.renderers.items.RunicWarstaffRenderer();
                }
                return renderer;
            }
        });
    }
}
