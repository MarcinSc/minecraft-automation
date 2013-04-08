package com.gempukku.minecraft.automation;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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

    @Override
    public int getSpriteNumber() {
        return 1;
    }

    // This is the icon that is rendered, when held in hand and on the ground
    @Override
    public Icon getIcon(ItemStack stack, int pass) {
        return _icon;
    }

    // This is the icon that is rendered, when in inventory
    @Override
    public Icon getIconFromDamage(int par1) {
        return _icon;
    }

    @Override
    public void updateIcons(IconRegister par1IconRegister) {
        _icon = par1IconRegister.registerIcon("computer");
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

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (placed) {
            int blockFacing = getBlockFacingForEntity(player);
            Automation._computerBlock.initializedBlockAfterPlaced(world, x, y, z, blockFacing, stack.getItemDamage(), player.getEntityName());
        }
        return placed;
    }

    private int getBlockFacingForEntity(Entity entity) {
        int playerFacing = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (playerFacing == 0)
        {
            return 2;
        }

        if (playerFacing == 1)
        {
            return 5;
        }

        if (playerFacing == 2)
        {
            return 3;
        }

        if (playerFacing == 3)
        {
            return 4;
        }

        return 0;
    }
}
