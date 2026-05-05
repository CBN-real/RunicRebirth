package com.github.interactivemagic.network;

import com.github.interactivemagic.InteractiveMagic;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPackets {

    private static final String VERSION = "1";

    private ModPackets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(InteractiveMagic.MODID).versioned(VERSION);

        // C2S
        registrar.playToServer(DrawStartC2SPacket.TYPE, DrawStartC2SPacket.STREAM_CODEC, DrawStartC2SPacket::handle);
        registrar.playToServer(DrawSubmitC2SPacket.TYPE, DrawSubmitC2SPacket.STREAM_CODEC, DrawSubmitC2SPacket::handle);
        registrar.playToServer(CancelDrawC2SPacket.TYPE, CancelDrawC2SPacket.STREAM_CODEC, CancelDrawC2SPacket::handle);
        registrar.playToServer(ClearStackC2SPacket.TYPE, ClearStackC2SPacket.STREAM_CODEC, ClearStackC2SPacket::handle);
        registrar.playToServer(SwitchStackC2SPacket.TYPE, SwitchStackC2SPacket.STREAM_CODEC, SwitchStackC2SPacket::handle);

        // S2C
        registrar.playToClient(StackChangedS2CPacket.TYPE, StackChangedS2CPacket.STREAM_CODEC, StackChangedS2CPacket::handle);
    }
}
