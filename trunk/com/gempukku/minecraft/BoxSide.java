package com.gempukku.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class BoxSide {
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int SOUTH = 2;
    public static final int NORTH = 3;
    public static final int EAST = 4;
    public static final int WEST = 5;

    public static int getEntityFacingHorizontal(Entity entity) {
        int entityFacing = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (entityFacing == 0)
            return NORTH;
        else if (entityFacing == 1)
            return EAST;
        else if (entityFacing == 2)
            return SOUTH;
        else
            return WEST;
    }

    public static int getOpposite(int side) {
        return (side / 2) * 2 + (side % 2 == 0 ? 1 : 0);
    }
}
