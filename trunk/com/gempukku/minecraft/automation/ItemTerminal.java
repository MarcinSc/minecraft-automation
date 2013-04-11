package com.gempukku.minecraft.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemTerminal extends Item {
    public ItemTerminal(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setUnlocalizedName("keyboard");
        this.setCreativeTab(CreativeTabs.tabTools);
    }
}
