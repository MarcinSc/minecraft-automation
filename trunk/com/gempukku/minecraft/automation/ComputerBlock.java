package com.gempukku.minecraft.automation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ComputerBlock extends Block {
    public ComputerBlock(int id) {
        super(id, Material.ground);
        setHardness(1.5F);
        setResistance(10.0F);
        setUnlocalizedName("computer");
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
        ComputerTileEntity computerTileEntity = (ComputerTileEntity) world.getBlockTileEntity(x, y, z);
        if (computerTileEntity != null)
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this, 1, computerTileEntity.getComputerId()));
        else
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this, 1, 0));

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        // We already dropped the items in breakBlock method, therefore not dropping here anything
        ArrayList<ItemStack> itemsDropped = new ArrayList<ItemStack>();
        return itemsDropped;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving livingEntity, ItemStack itemPlaced) {
        ComputerTileEntity computerEntity = createTileEntityFromItemStack(world, itemPlaced);
        world.setBlockTileEntity(x, y, z, computerEntity);
    }

    private ComputerTileEntity createTileEntityFromItemStack(World world, ItemStack itemPlaced) {
        ComputerTileEntity result = new ComputerTileEntity();
        int computerId = itemPlaced.getItemDamage();
        // If it's a new computer, we have to assign an id to it, but only on server side
        if (computerId == 0 && !world.isRemote)
            computerId = Automation.getRegistry().assignNextComputerId();
        result.setComputerId(computerId);
        result.validate();
        return result;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new ComputerTileEntity();
    }
}
