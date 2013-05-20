package com.gempukku.minecraft.automation.item;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.block.ComputerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ComputerItemBlock extends ItemBlock {
    private Icon _icon;
    private ComputerBlock _computerBlock;

    public ComputerItemBlock(int id, Block computerBlock) {
        super(id);
        _computerBlock = (ComputerBlock) computerBlock;
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    private String getComputerIconToRegister() {
        return _computerBlock.getComputerFrontReadyIcon();
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
    public void registerIcons(IconRegister par1IconRegister) {
        _icon = par1IconRegister.registerIcon(getComputerIconToRegister());
    }

    private String getComputerLabel(ItemStack itemStack) {
        return Automation.proxy.getRegistry().getComputerLabel(itemStack.getItemDamage());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        int blockFacing = getBlockFacingForEntity(player);
        boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (placed)
            Automation.personalComputerBlock.initializeBlockAfterPlaced(world, x, y, z, stack.getItemDamage(), player.getEntityName(), blockFacing);

        return placed;
    }

    private int getBlockFacingForEntity(Entity entity) {
        return BoxSide.getOpposite(BoxSide.getEntityFacingHorizontal(entity));
    }
}
