package com.gempukku.minecraft.automation.module.gps;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class GpsModuleItem extends Item {
    public GpsModuleItem(int id) {
        super(id);
        setUnlocalizedName("gpsModule");
        setCreativeTab(CreativeTabs.tabMisc);
    }
}
