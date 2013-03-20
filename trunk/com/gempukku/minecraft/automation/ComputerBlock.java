package com.gempukku.minecraft.automation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ComputerBlock extends Block {
    public ComputerBlock(int id, Material material) {
        super(id, material);
        this.setCreativeTab(CreativeTabs.tabBlock);
        System.out.println("Computer block");
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float directionX, float directionY, float directionZ) {
        if (world.isRemote) {
            ComputerTileEntity computerTile = (ComputerTileEntity) world.getBlockTileEntity(x, y, z);
            computerTile.setComputerId(computerTile.getComputerId() + 1);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving livingEntity, ItemStack itemPlaced) {
        if (world.isRemote)
            world.setBlockTileEntity(x, y, z, createTileEntityFromItemStack(itemPlaced));
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int type, EntityPlayer player) {
        super.onBlockHarvested(world, x, y, z, type, player);
    }

    private TileEntity createTileEntityFromItemStack(ItemStack itemPlaced) {
        ComputerTileEntity result = new ComputerTileEntity();
        result.setComputerId(itemPlaced.getItemDamage());
        return result;
    }

    @Override
    public void onPostBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {
        System.out.println("Post block placed" + " remote=" + par1World.isRemote);
        super.onPostBlockPlaced(par1World, par2, par3, par4, par5);
    }
}
