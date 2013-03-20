package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class ComputerItemBlock extends ItemBlock {
    public ComputerItemBlock(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean par4) {
        String displayName = getComputerLabel(itemStack);
        if (displayName == null)
            displayName = "-Unlabeled-";
        lines.add(EnumChatFormatting.RED + displayName);
    }

    private String getComputerLabel(ItemStack itemStack) {
        int computerId = itemStack.getItemDamage();
        if (computerId == 0)
            return null;
        ComputerData computerData = AutomationRegistry.getComputerData(computerId);
        if (computerData == null || computerData.getLabel() == null)
            return null;
        return computerData.getLabel();
    }
}
