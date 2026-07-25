package com.github.runicrebirth.network;

import com.github.runicrebirth.RunicRebirth;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPackets {

    private static final String VERSION = "1";

    private ModPackets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RunicRebirth.MODID).versioned(VERSION);

        // C2S
        registrar.playToServer(DrawStartC2SPacket.TYPE, DrawStartC2SPacket.STREAM_CODEC, DrawStartC2SPacket::handle);
        registrar.playToServer(DrawSubmitC2SPacket.TYPE, DrawSubmitC2SPacket.STREAM_CODEC, DrawSubmitC2SPacket::handle);
        registrar.playToServer(CancelDrawC2SPacket.TYPE, CancelDrawC2SPacket.STREAM_CODEC, CancelDrawC2SPacket::handle);
        registrar.playToServer(CanvasSelectElementC2SPacket.TYPE, CanvasSelectElementC2SPacket.STREAM_CODEC, CanvasSelectElementC2SPacket::handle);
        registrar.playToServer(CanvasSelectTierC2SPacket.TYPE, CanvasSelectTierC2SPacket.STREAM_CODEC, CanvasSelectTierC2SPacket::handle);
        registrar.playToServer(ClearStackC2SPacket.TYPE, ClearStackC2SPacket.STREAM_CODEC, ClearStackC2SPacket::handle);
        registrar.playToServer(SwitchStackC2SPacket.TYPE, SwitchStackC2SPacket.STREAM_CODEC, SwitchStackC2SPacket::handle);
        registrar.playToServer(FinalizeCircuitC2SPacket.TYPE, FinalizeCircuitC2SPacket.STREAM_CODEC, FinalizeCircuitC2SPacket::handle);

        // S2C
        registrar.playToClient(StackChangedS2CPacket.TYPE, StackChangedS2CPacket.STREAM_CODEC, StackChangedS2CPacket::handle);
        registrar.playToClient(ImpactEffectS2CPacket.TYPE, ImpactEffectS2CPacket.STREAM_CODEC, ImpactEffectS2CPacket::handle);
        registrar.playToClient(SpellUnlockSyncS2CPacket.TYPE, SpellUnlockSyncS2CPacket.STREAM_CODEC, SpellUnlockSyncS2CPacket::handle);
        registrar.playToClient(CastAnimBroadcastS2CPacket.TYPE, CastAnimBroadcastS2CPacket.STREAM_CODEC, CastAnimBroadcastS2CPacket::handle);

        // Dungeon system
        registrar.playToServer(EnterDungeonC2SPacket.TYPE, EnterDungeonC2SPacket.STREAM_CODEC, EnterDungeonC2SPacket::handle);
        registrar.playToServer(SelectDungeonC2SPacket.TYPE, SelectDungeonC2SPacket.STREAM_CODEC, SelectDungeonC2SPacket::handle);
        registrar.playToClient(DungeonDataSyncS2CPacket.TYPE, DungeonDataSyncS2CPacket.STREAM_CODEC, DungeonDataSyncS2CPacket::handle);
        registrar.playToClient(OpenDungeonScreenS2CPacket.TYPE, OpenDungeonScreenS2CPacket.STREAM_CODEC, OpenDungeonScreenS2CPacket::handle);
    }
}
