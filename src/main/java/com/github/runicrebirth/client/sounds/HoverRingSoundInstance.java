package com.github.runicrebirth.client.sounds;

import com.github.runicrebirth.client.ClientMagicData;
import com.github.runicrebirth.init.ModSounds;
import com.github.runicrebirth.items.curios.HoverRingItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoverRingSoundInstance extends AbstractTickableSoundInstance {

    private final LocalPlayer player;

    public HoverRingSoundInstance(LocalPlayer player) {
        super(ModSounds.SPELLS_HOVER.get(), SoundSource.PLAYERS, RandomSource.create());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1f;
        this.pitch = 0.7f;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return ClientMagicData.ringDurationRemaining(HoverRingItem.DURATION_KEY) != 0;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (ClientMagicData.ringDurationRemaining(HoverRingItem.DURATION_KEY) == 0) {
            stop();
            return;
        }
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}
