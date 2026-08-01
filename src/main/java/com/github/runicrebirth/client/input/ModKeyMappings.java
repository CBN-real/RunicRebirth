package com.github.runicrebirth.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public final class ModKeyMappings {

    public static final String CATEGORY = "key.categories.runicrebirth";

    public static final KeyMapping SWITCH_SPELL_STACK = new KeyMapping(
        "key.runicrebirth.switch_spell_stack",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        CATEGORY
    );

    // Index 0=thumb, 1=index, 2=middle, 3=ring, 4=pinkie
    public static final KeyMapping[] ACTIVATE_SPELL_RINGS = {
        new KeyMapping("key.runicrebirth.activate_thumb_ring", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY),
        new KeyMapping("key.runicrebirth.activate_index_ring", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY),
        new KeyMapping("key.runicrebirth.activate_middle_ring", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY),
        new KeyMapping("key.runicrebirth.activate_ring_ring", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY),
        new KeyMapping("key.runicrebirth.activate_pinkie_ring", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY)
    };

    private ModKeyMappings() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_SPELL_STACK);
        for (KeyMapping ring : ACTIVATE_SPELL_RINGS) {
            event.register(ring);
        }
    }

    public static boolean isMovementKey(int keyCode, int scanCode) {
        Minecraft mc = Minecraft.getInstance();
        KeyMapping[] movement = new KeyMapping[]{
            mc.options.keyUp,
            mc.options.keyDown,
            mc.options.keyLeft,
            mc.options.keyRight,
            mc.options.keyJump,
            mc.options.keyShift
        };
        InputConstants.Key pressed = InputConstants.getKey(keyCode, scanCode);
        for (KeyMapping m : movement) {
            if (m.getKey().equals(pressed)) return true;
        }
        return false;
    }
}
