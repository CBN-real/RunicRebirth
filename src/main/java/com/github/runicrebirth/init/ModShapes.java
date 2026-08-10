package com.github.runicrebirth.init;

import com.github.runicrebirth.api.registry.ShapeRegistry;
import com.github.runicrebirth.magic.recognition.Recognizers;
import com.github.runicrebirth.magic.recognition.ShapeTemplates;

/**
 * Binds hand-authored shape templates to SpellComponents with per-shape recognition thresholds.
 * Thresholds tuned empirically — $P score range varies wildly per template complexity.
 */
public final class ModShapes {

    private ModShapes() {}

    public static void init() {
        ShapeRegistry.register(ShapeTemplates.ID_LINE_DOWN, ShapeTemplates.lineDown(), -0.1,
            ModSpellTypes.MAGIC_PROJECTILE::get);
        ShapeRegistry.register(ShapeTemplates.ID_CIRCLE, ShapeTemplates.circle(), -0.5,
            ModSpellTypes.MAGIC_BLAST::get);
        ShapeRegistry.register(ShapeTemplates.ID_V, ShapeTemplates.vShape(), -0.75,
            ModSpellTypes.MAGIC_BEAM::get);
        ShapeRegistry.register(ShapeTemplates.ID_PLUS, ShapeTemplates.plus(), -0.5,
            ModModifiers.SIZE_PLUS::get);
        ShapeRegistry.register(ShapeTemplates.ID_ARROW, ShapeTemplates.arrow(), -2.0,
            ModSpellTypes.MAGIC_ARROW::get);
        ShapeRegistry.register(ShapeTemplates.ID_INFUSION, ShapeTemplates.infusion(), -1.1,
            ModSpellTypes.INFUSION::get);
        ShapeRegistry.register(ShapeTemplates.ID_EXPLOSION, ShapeTemplates.explosion(), -1.2,
            ModSpellTypes.MAGIC_EXPLOSION::get);
        ShapeRegistry.register(ShapeTemplates.ID_SLASH, ShapeTemplates.slash(), -1.0,
            ModSpellTypes.MAGIC_SLASH::get);
        ShapeRegistry.register(ShapeTemplates.ID_METEOR, ShapeTemplates.meteor(), -1.0,
            ModSpellTypes.MAGIC_METEOR::get);
        ShapeRegistry.register(ShapeTemplates.ID_SHIELD, ShapeTemplates.shield(), -1.0,
            ModSpellTypes.MAGIC_SHIELD::get);
        ShapeRegistry.register(ShapeTemplates.ID_HAMMER, ShapeTemplates.hammer(), -1.0,
            ModSpellTypes.MAGIC_HAMMER::get);
        ShapeRegistry.register(ShapeTemplates.ID_BINDING, ShapeTemplates.binding(), -1.0,
            ModSpellTypes.MAGIC_BINDING::get);
        ShapeRegistry.register(ShapeTemplates.ID_BALLISTA, ShapeTemplates.ballista(), -1.0,
            ModSpellTypes.MAGIC_BALLISTA::get);
        ShapeRegistry.register(ShapeTemplates.ID_COOLDOWN, ShapeTemplates.cooldown(), -1.0,
            ModModifiers.COOLDOWN::get);
        ShapeRegistry.register(ShapeTemplates.ID_RANGE, ShapeTemplates.range(), -1.0,
            ModModifiers.RANGE::get);
        ShapeRegistry.register(ShapeTemplates.ID_TWO_CASTS, ShapeTemplates.two_casts(), -1.0,
            ModModifiers.TWO_CASTS::get);
        ShapeRegistry.register(ShapeTemplates.ID_FOUR_CASTS, ShapeTemplates.four_casts(), -1.0,
            ModModifiers.FOUR_CASTS::get);
        ShapeRegistry.register(ShapeTemplates.ID_CHARGES, ShapeTemplates.charges(), -1.0,
            ModModifiers.CHARGES::get);
        ShapeRegistry.register(ShapeTemplates.ID_CHARGES_THREE, ShapeTemplates.threeCharges(), -1.0,
            ModModifiers.CHARGES_THREE::get);
        ShapeRegistry.register(ShapeTemplates.ID_CHARGES_FOUR, ShapeTemplates.fourCharges(), -1.0,
            ModModifiers.CHARGES_FOUR::get);
        ShapeRegistry.register(ShapeTemplates.ID_PLUS_TWO, ShapeTemplates.plus_two(), -1.0,
            ModModifiers.SIZE_PLUS_TWO::get);
        ShapeRegistry.register(ShapeTemplates.ID_PLUS_FOUR, ShapeTemplates.plus_four(), -1.0,
            ModModifiers.SIZE_PLUS_FOUR::get);
        ShapeRegistry.register(ShapeTemplates.ID_SHARP_BOOST, ShapeTemplates.sharpBoost(), -1.0,
            ModModifiers.SHARP_BOOST::get);
        ShapeRegistry.register(ShapeTemplates.ID_BLUNT_BOOST, ShapeTemplates.bluntBoost(), -1.0,
            ModModifiers.BLUNT_BOOST::get);
        ShapeRegistry.register(ShapeTemplates.ID_MAGIC_BOOST, ShapeTemplates.magicBoost(), -1.0,
            ModModifiers.MAGIC_BOOST::get);
        Recognizers.rebuild();
    }
}
