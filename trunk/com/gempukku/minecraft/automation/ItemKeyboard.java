package com.gempukku.minecraft.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemKeyboard extends Item {
    public ItemKeyboard(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("keyboard");
        this.setCreativeTab(CreativeTabs.tabTools);
    }
}
