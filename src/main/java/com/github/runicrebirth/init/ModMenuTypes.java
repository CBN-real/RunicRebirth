package com.github.runicrebirth.init;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.menu.RunicKeyRingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, RunicRebirth.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<RunicKeyRingMenu>> RUNIC_KEY_RING =
        MENU_TYPES.register("runic_key_ring",
            () -> IMenuTypeExtension.create(
                (windowId, inv, buf) -> new RunicKeyRingMenu(windowId, inv, buf.readVarInt())
            ));

    private ModMenuTypes() {}
}
