package com.gempukku.minecraft.automation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;

import java.util.List;

public class ComputerItemBlock extends ItemBlock {
    private Icon _icon;

    public ComputerItemBlock(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean par4) {
        String displayName = getComputerLabel(itemStack);
        if (displayName == null)
            displayName = "-Unlabeled-";
        lines.add(EnumChatFormatting.RED + displayName);
    }

    @SideOnly(Side.CLIENT)
    public void func_94581_a(IconRegister iconRegister)
    {
        _icon = iconRegister.func_94245_a("computer");
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

//    @Override
//    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
//        System.out.println("Item right clicked");
//        return super.onItemRightClick(par1ItemStack, par2World, par3EntityPlayer);
//    }
}
