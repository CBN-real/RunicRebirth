package com.github.runicrebirth.client.sounds;

import com.github.runicrebirth.entities.spells.EnergyCracklingEntity;
import com.github.runicrebirth.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnergyCracklingSoundInstance extends AbstractTickableSoundInstance {

    private final EnergyCracklingEntity entity;

    public EnergyCracklingSoundInstance(EnergyCracklingEntity entity) {
        super(ModSounds.SPELLS_ENERGY_CRACKLING.get(), SoundSource.AMBIENT, RandomSource.create());
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.6f;
        this.pitch = 1.0f;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    @Override
    public boolean canPlaySound() {
        return !entity.isRemoved();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (entity.isRemoved()) {
            stop();
            return;
        }
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }
}
