package com.gempukku.minecraft.automation;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ComputerItemBlock extends ItemBlock {
    public ComputerItemBlock(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBlock);
        System.out.println("Computer item block created");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean par4) {
        String displayName = getComputerLabel(itemStack);
        if (displayName == null)
            displayName = "-Unlabeled-";
        lines.add(EnumChatFormatting.RED + displayName);
    }

    private String getComputerLabel(ItemStack itemStack) {
        return String.valueOf(itemStack.getItemDamage());
//        int computerId = itemStack.getItemDamage();
//        if (computerId == 0)
//            return null;
//        ComputerData computerData = AutomationRegistry.getComputerData(computerId);
//        if (computerData == null || computerData.getLabel() == null)
//            return null;
//        return computerData.getLabel();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        System.out.println("Item right clicked");
        return super.onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
    }
}
