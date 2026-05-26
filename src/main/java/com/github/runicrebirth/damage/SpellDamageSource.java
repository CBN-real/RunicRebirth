package com.github.runicrebirth.damage;

import com.github.runicrebirth.api.damage.ISpellDamageSource;
import com.github.runicrebirth.api.spells.Element;
import com.github.runicrebirth.api.spells.MagicDamageType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpellDamageSource extends DamageSource implements ISpellDamageSource {

    private final MagicDamageType magicDamageType;
    private final Element element;
    private float lifesteal;
    private int freezeTicks;
    private int fireTime;
    private int iFrames = -1;

    protected SpellDamageSource(@NotNull Entity direct, @NotNull Entity causing,
                                @Nullable Vec3 pos, MagicDamageType magicDamageType, Element element) {
        super(getHolderFromResource(direct, magicDamageType.damageTypeKey()), direct, causing, pos);
        this.magicDamageType = magicDamageType;
        this.element = element;
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity target) {
        String key = "death.attack." + magicDamageType.damageTypeKey().location().getPath();
        Entity causing = this.getEntity();
        Entity direct = this.getDirectEntity();
        Component src = causing == null
            ? (direct == null ? target.getDisplayName() : direct.getDisplayName())
            : causing.getDisplayName();
        return Component.translatable(key, target.getDisplayName(), src);
    }

    private static Holder<DamageType> getHolderFromResource(Entity entity, ResourceKey<DamageType> key) {
        java.util.Optional<Holder.Reference<DamageType>> option =
            entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(key);
        if (option.isPresent()) return option.get();
        return entity.level().damageSources().genericKill().typeHolder();
    }

    public static SpellDamageSource source(@NotNull Entity causing, MagicDamageType damageType, Element element) {
        return source(causing, causing, damageType, element);
    }

    public static SpellDamageSource source(@NotNull Entity direct, @NotNull Entity causing,
                                           MagicDamageType damageType, Element element) {
        return new SpellDamageSource(direct, causing, null, damageType, element);
    }

    public SpellDamageSource setLifestealPercent(float v) { this.lifesteal = v; return this; }
    public SpellDamageSource setFireTicks(int v) { this.fireTime = v; return this; }
    public SpellDamageSource setFreezeTicks(int v) { this.freezeTicks = v; return this; }
    public SpellDamageSource setIFrames(int v) { this.iFrames = v; return this; }

    @Override public MagicDamageType magicDamageType() { return magicDamageType; }
    @Override public Element element() { return element; }
    @Override public float lifestealPercent() { return lifesteal; }
    @Override public int fireTicks() { return fireTime; }
    @Override public int freezeTicks() { return freezeTicks; }
    @Override public int iFrames() { return iFrames; }

    @Override
    public boolean hasPostHitEffects() {
        return lifesteal > 0 || fireTime > 0 || freezeTicks > 0 || element != null;
    }
}
