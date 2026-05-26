package com.github.runicrebirth.init;

import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.spells.types.MagicArrow;
import com.github.runicrebirth.spells.types.MagicBallista;
import com.github.runicrebirth.spells.types.MagicBeam;
import com.github.runicrebirth.spells.types.MagicBinding;
import com.github.runicrebirth.spells.types.MagicBlast;
import com.github.runicrebirth.spells.types.MagicExplosion;
import com.github.runicrebirth.spells.types.MagicHammer;
import com.github.runicrebirth.spells.types.MagicMeteor;
import com.github.runicrebirth.spells.types.MagicProjectile;
import com.github.runicrebirth.spells.types.MagicShield;
import com.github.runicrebirth.spells.types.MagicSlash;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModSpellTypes {

    public static final DeferredHolder<SpellType, MagicProjectile> MAGIC_PROJECTILE =
        SpellTypeRegistry.SPELL_TYPES.register("magic_projectile", MagicProjectile::new);

    public static final DeferredHolder<SpellType, MagicBeam> MAGIC_BEAM =
        SpellTypeRegistry.SPELL_TYPES.register("magic_beam", MagicBeam::new);

    public static final DeferredHolder<SpellType, MagicBlast> MAGIC_BLAST =
        SpellTypeRegistry.SPELL_TYPES.register("magic_blast", MagicBlast::new);

    public static final DeferredHolder<SpellType, MagicArrow> MAGIC_ARROW =
        SpellTypeRegistry.SPELL_TYPES.register("magic_arrow", MagicArrow::new);

    public static final DeferredHolder<SpellType, MagicExplosion> MAGIC_EXPLOSION =
        SpellTypeRegistry.SPELL_TYPES.register("magic_explosion", MagicExplosion::new);

    public static final DeferredHolder<SpellType, MagicSlash> MAGIC_SLASH =
        SpellTypeRegistry.SPELL_TYPES.register("magic_slash", MagicSlash::new);

    public static final DeferredHolder<SpellType, MagicMeteor> MAGIC_METEOR =
        SpellTypeRegistry.SPELL_TYPES.register("magic_meteor", MagicMeteor::new);

    public static final DeferredHolder<SpellType, MagicShield> MAGIC_SHIELD =
        SpellTypeRegistry.SPELL_TYPES.register("magic_shield", MagicShield::new);

    public static final DeferredHolder<SpellType, MagicHammer> MAGIC_HAMMER =
        SpellTypeRegistry.SPELL_TYPES.register("magic_hammer", MagicHammer::new);

    public static final DeferredHolder<SpellType, MagicBinding> MAGIC_BINDING =
        SpellTypeRegistry.SPELL_TYPES.register("magic_binding", MagicBinding::new);

    public static final DeferredHolder<SpellType, MagicBallista> MAGIC_BALLISTA =
        SpellTypeRegistry.SPELL_TYPES.register("magic_ballista", MagicBallista::new);

    private ModSpellTypes() {}

    public static void init() {}
}
