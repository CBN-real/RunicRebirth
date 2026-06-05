package com.github.runicrebirth.client;

import org.jetbrains.annotations.Nullable;

public final class BookDisplayState {

    private static @Nullable String selectedElement = null;
    private static float offsetX = 0f;
    private static float offsetY = 0f;
    private static float offsetZ = 0f;

    private BookDisplayState() {}

    public static @Nullable String getSelectedElement() {
        return selectedElement;
    }

    public static void setSelectedElement(@Nullable String element) {
        selectedElement = element;
    }

    public static float getOffsetX() {
        return offsetX;
    }

    public static float getOffsetY() {
        return offsetY;
    }

    public static float getOffsetZ() {
        return offsetZ;
    }

    public static void setOffsets(float x, float y, float z) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
    }

    public static void reset() {
        selectedElement = null;
        offsetX = 0f;
        offsetY = 0f;
        offsetZ = 0f;
    }
}
