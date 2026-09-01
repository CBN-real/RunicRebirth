package com.github.runicrebirth.client.sounds;

import com.github.runicrebirth.entities.spells.MagicBindingEntity;
import com.github.runicrebirth.entities.spells.SpellPhase;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class MagicBindingSoundInstance extends AbstractTickableSoundInstance {

    private final MagicBindingEntity entity;

    public MagicBindingSoundInstance(MagicBindingEntity entity) {
        super(ModSounds.SPELLS_HOLD_BINDING.get(), SoundSource.PLAYERS, RandomSource.create());
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    @Override
    public boolean canPlaySound() {
        return !entity.isRemoved() && entity.getPhase() != SpellPhase.ENDING;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (entity.isRemoved() || entity.getPhase() == SpellPhase.ENDING) {
            stop();
            return;
        }
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }
}
