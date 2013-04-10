package com.gempukku.minecraft;

import net.minecraft.world.World;

public class MinecraftUtils {
    public static boolean isClient(World world) {
        return !world.isRemote;
    }

    public static boolean isServer(World world) {
        return world.isRemote;
    }
}
