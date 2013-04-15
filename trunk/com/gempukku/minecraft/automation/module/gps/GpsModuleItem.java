package com.gempukku.minecraft.automation.module.gps;

import com.gempukku.minecraft.automation.Automation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GpsModuleItem extends Item {
    public GpsModuleItem(int id) {
        super(id);
        setUnlocalizedName("computerModule");
        setCreativeTab(CreativeTabs.tabMisc);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(int itemId, CreativeTabs creativeTab, List listOfStacks) {
        for (int metadata : Automation.proxy.getRegistry().getModuleItemMetadataForItem(itemId))
            listOfStacks.add(new ItemStack(itemId, 1, metadata));
    }
}
