package com.gempukku.minecraft.automation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ComputerBlock extends Block {
    private Icon _frontWorkingIcon;
    private Icon _frontReadyIcon;
    private Icon _sideIcon;

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

    @SideOnly(Side.CLIENT)
    public void func_94332_a(IconRegister iconRegister)
    {
        _frontReadyIcon = iconRegister.func_94245_a("computerFrontReady");
        _frontWorkingIcon = iconRegister.func_94245_a("computerFrontWorking");
        _sideIcon = iconRegister.func_94245_a("computerSide");
    }


    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        // We already dropped the items in breakBlock method, therefore not dropping here anything
        ArrayList<ItemStack> itemsDropped = new ArrayList<ItemStack>();
        return itemsDropped;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving livingEntity, ItemStack itemPlaced) {
        int facing = MathHelper.floor_double((double) (livingEntity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ComputerTileEntity computerEntity = createTileEntityFromItemStack(world, itemPlaced, facing);
        world.setBlockTileEntity(x, y, z, computerEntity);
    }

    private ComputerTileEntity createTileEntityFromItemStack(World world, ItemStack itemPlaced, int facing) {
        ComputerTileEntity result = new ComputerTileEntity();
        int computerId = itemPlaced.getItemDamage();
        // If it's a new computer, we have to assign an id to it, but only on server side
        if (computerId == 0 && !world.isRemote)
            computerId = Automation.getRegistry().assignNextComputerId();
        result.setComputerId(computerId);
        result.setFacing(facing);
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
