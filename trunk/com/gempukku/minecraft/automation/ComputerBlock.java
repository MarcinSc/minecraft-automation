package com.gempukku.minecraft.automation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
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

    public Icon get_frontReadyIcon() {
        return _frontReadyIcon;
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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        _frontReadyIcon = iconRegister.registerIcon("computerFrontReady");
        _frontWorkingIcon = iconRegister.registerIcon("computerFrontWorking");
        _sideIcon = iconRegister.registerIcon("computerSide");
    }

    @Override
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        final ComputerTileEntity tileEntity = (ComputerTileEntity) blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity != null && side == tileEntity.getFacing()) {
            if (Automation.getProgramProcessing().isRunningProgram(tileEntity.getComputerId()))
                return _frontWorkingIcon;
            else
                return _frontReadyIcon;
        }

        return _sideIcon;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        // We already dropped the items in breakBlock method, therefore not dropping here anything
        ArrayList<ItemStack> itemsDropped = new ArrayList<ItemStack>();
        return itemsDropped;
    }

    private ComputerTileEntity createTileEntityFromItemStack(World world, int computerId, int facing) {
        ComputerTileEntity result = new ComputerTileEntity();
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

    public void initializedBlockAfterPlaced(World world, int x, int y, int z, int facing, int computerId) {
        ComputerTileEntity computerEntity = createTileEntityFromItemStack(world, computerId, facing);
        world.setBlockTileEntity(x, y, z, computerEntity);
    }
}
