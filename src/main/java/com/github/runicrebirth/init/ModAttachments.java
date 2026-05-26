package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.capabilities.magic.MagicData;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RunicRebirth.MODID);

    public static final Supplier<AttachmentType<MagicData>> MAGIC_DATA = ATTACHMENTS.register(
        "magic_data",
        () -> AttachmentType.builder(MagicData::new)
            .serialize(MagicData.CODEC)
            .copyOnDeath()
            .build()
    );

    private ModAttachments() {}
}
