package com.github.tanconan.observing_eyeblossom.client;

import net.minecraft.world.entity.Entity;

public final class RenderingEntityContext {
    private static Entity current;

    public static void set(Entity entity) { current = entity; }
    public static void clear() { current = null; }
    public static Entity get() { return current; }

    private RenderingEntityContext() {}
}
