package com.github.runicrebirth.client.renderers;

import com.geckolib.constant.dataticket.DataTicket;

public final class ModDataTickets {

    // Lerped head pitch in radians, pre-computed from entity xRot/xRotO + partialTick.
    public static final DataTicket<Float> HEAD_PITCH_RAD =
        DataTicket.create("runicrebirth.head_pitch_rad", Float.class);

    // Lerped net head yaw in radians, pre-computed from entity yHeadRot/yBodyRot + partialTick.
    public static final DataTicket<Float> HEAD_YAW_RAD =
        DataTicket.create("runicrebirth.head_yaw_rad", Float.class);

    // Whether the entity has not yet been added to the level (i.e. rendering in a GUI/book preview).
    public static final DataTicket<Boolean> RENDER_IN_BOOK =
        DataTicket.create("runicrebirth.render_in_book", Boolean.class);

    // Pre-scaled render scale for AbstractCircleEntity (circle scale, spell-height adjusted).
    public static final DataTicket<Float> CIRCLE_RENDER_SCALE =
        DataTicket.create("runicrebirth.circle_render_scale", Float.class);

    // Pre-scaled render scale for ScaledSpellEntity (projectile size).
    public static final DataTicket<Float> PROJECTILE_RENDER_SCALE =
        DataTicket.create("runicrebirth.projectile_render_scale", Float.class);

    // Lerped entity yaw in degrees, pre-computed from entity yRotO/getYRot + partialTick.
    public static final DataTicket<Float> ENTITY_Y_ROT_LERP =
        DataTicket.create("runicrebirth.entity_y_rot_lerp", Float.class);

    // Lerped entity pitch in degrees, pre-computed from entity xRotO/getXRot + partialTick.
    public static final DataTicket<Float> ENTITY_X_ROT_LERP =
        DataTicket.create("runicrebirth.entity_x_rot_lerp", Float.class);

    private ModDataTickets() {}
}
