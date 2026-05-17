package com.github.interactivemagic.items.armor;

import com.github.interactivemagic.api.item.ISpellEmpowerment;
import com.github.interactivemagic.api.spells.SpellCastContext;
import com.github.interactivemagic.api.spells.SpellModifier;
import com.github.interactivemagic.client.renderers.armor.MagicArmorRenderer;
import com.github.interactivemagic.client.renderers.models.MagicArmorGeoModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class MagicArmorItem extends ArmorItem implements GeoItem, ISpellEmpowerment {

    private final float magicResistance;
    private final float bluntResistance;
    private final float sharpResistance;
    private final List<SpellModifier> modifiers;
    protected final String armorName;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MagicArmorItem(Holder<ArmorMaterial> material, Type type, Properties props,
                          String armorName,
                          float magicRes, float bluntRes, float sharpRes,
                          List<SpellModifier> modifiers) {
        super(material, type, props);
        this.armorName = armorName;
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
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    @Nullable T livingEntity, ItemStack itemStack,
                    @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) {
                    this.renderer = supplyRenderer();
                }
                return this.renderer;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    protected GeoArmorRenderer<?> supplyRenderer() {
        return new MagicArmorRenderer<>(new MagicArmorGeoModel<>(armorName));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 20, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
