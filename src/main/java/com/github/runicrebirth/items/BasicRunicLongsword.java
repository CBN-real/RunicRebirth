package com.github.runicrebirth.items;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.api.item.IMagicWeapon;
import com.github.runicrebirth.network.RunicWeaponAnimS2CPacket;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellStack;
import com.github.runicrebirth.capabilities.magic.MagicData;
import com.github.runicrebirth.entities.spells.MagicSlashEntity;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.init.ModSpellTypes;
import com.github.runicrebirth.magic.stack.SpellResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;



import java.util.function.Consumer;

public class BasicRunicLongsword extends Item implements GeoItem, IMagicWeapon {

    public static final Identifier COOLDOWN_ID =
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "basic_runic_longsword");
    public static final int COOLDOWN_TICKS = 200;

    private static final ToolMaterial TIER = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL, 300, 6.0f, 4.0f, 14, ItemTags.IRON_TOOL_MATERIALS
    );

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BasicRunicLongsword(Item.Properties properties) {
        super(TIER.applySwordProperties(properties.stacksTo(1), 3.0f, -2.4f));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public void activate(ServerPlayer sp) {
        MagicData data = MagicData.of(sp);
        if (data.isOnCooldown(COOLDOWN_ID)) return;

        Level level = sp.level();
        Vec3 eye = sp.getEyePosition();
        Vec3 dir = sp.getLookAngle().normalize();
        SpellCastContext ctx = new SpellCastContext(
            (ServerLevel) level, sp, sp.getMainHandItem(), eye, dir, sp.getXRot(), sp.getYRot(), null);

        SpellStack tmp = new SpellStack();
        tmp.append(ModSpellTypes.MAGIC_SLASH.get());
        var params = SpellResolver.buildParams(ctx, tmp);
        if (params == null) return;

        com.github.runicrebirth.api.spells.Element runeElement =
            com.github.runicrebirth.rune.RuneEffectApplicator.getActiveElement(sp.getMainHandItem());
        if (runeElement != null) params.element = runeElement;

        level.playSound(null, sp.getX(), sp.getY(), sp.getZ(),
            ModSounds.SPELLS_LONGSWORD.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
        MagicSlashEntity slash = new MagicSlashEntity(level, sp, params, dir);
        slash.setChargeTicks(10);
        Vec3 spawnPos = eye.add(dir.scale(1.0));
        slash.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        level.addFreshEntity(slash);

        data.startCooldown(COOLDOWN_ID, COOLDOWN_TICKS);
        RunicWeaponAnimS2CPacket.send(sp, RunicWeaponAnimS2CPacket.Anim.SWORD_SWING);
    }

    @Override
    public Identifier getWeaponCooldownId() { return COOLDOWN_ID; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<BasicRunicLongsword>("controller", 5,
            state -> { state.setAnimation(IDLE); return PlayState.CONTINUE; }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<BasicRunicLongsword> renderer;

            @Override
            public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.github.runicrebirth.client.renderers.items.BasicRunicLongswordRenderer();
                }
                return this.renderer;
            }
        });
    }
}
