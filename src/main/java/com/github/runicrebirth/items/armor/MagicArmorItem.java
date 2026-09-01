package com.github.runicrebirth.items.armor;

import com.github.runicrebirth.api.item.ISpellEmpowerment;
import com.github.runicrebirth.api.spells.SpellCastContext;
import com.github.runicrebirth.api.spells.SpellModifier;
import com.github.runicrebirth.client.renderers.armor.MagicArmorRenderer;
import com.github.runicrebirth.client.renderers.models.MagicArmorGeoModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

// TODO: Register ResourceKey<EquipmentAsset> for magic armor â€” create assets/runicrebirth/models/equipment/magic_armor.json
// TODO: Verify GeckoLib GeoArmorRenderer compatibility with Item base (no longer ArmorItem) in updated GeckoLib build
public class MagicArmorItem extends Item implements GeoItem, ISpellEmpowerment {

    private final float magicResistance;
    private final float bluntResistance;
    private final float sharpResistance;
    private final List<SpellModifier> modifiers;
    protected final String armorName;
    protected final String textureName;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // props must have DataComponents.MAX_DAMAGE, EQUIPPABLE (with slot + EquipmentAsset),
    // ATTRIBUTE_MODIFIERS, REPAIRABLE, and ENCHANTABLE set by the caller.
    // Example:
    //   new Item.Properties()
    //       .component(DataComponents.MAX_DAMAGE, durability)
    //       .component(DataComponents.EQUIPPABLE, Equippable.builder(slot)
    //           .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
    //           .setAsset(EQUIPMENT_ASSET_KEY)  // TODO: register ResourceKey<EquipmentAsset>
    //           .build())
    //       .component(DataComponents.ENCHANTABLE, new Enchantable(enchantmentValue))
    public MagicArmorItem(Properties props,
                          String armorName, String textureName,
                          float magicRes, float bluntRes, float sharpRes,
                          List<SpellModifier> modifiers) {
        super(props);
        this.armorName = armorName;
        this.textureName = textureName;
        this.magicResistance = magicRes;
        this.bluntResistance = bluntRes;
        this.sharpResistance = sharpRes;
        this.modifiers = List.copyOf(modifiers);
    }

    public float magicResistance() { return magicResistance; }
    public float bluntResistance() { return bluntResistance; }
    public float sharpResistance() { return sharpResistance; }

    @Override
    public List<SpellModifier> contribute(ItemStack accessoryStack, SpellCastContext ctx) {
        return modifiers;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                if (this.renderer == null) {
                    this.renderer = supplyRenderer();
                }
                return this.renderer;
            }
        });
    }

    protected GeoArmorRenderer<?, ?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName, textureName));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<MagicArmorItem>("controller", 10, state -> {
            boolean moving = Boolean.TRUE.equals(state.getData(DataTickets.IS_MOVING));
            return state.setAndContinue(moving
                    ? RawAnimation.begin().thenLoop("walk")
                    : RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
