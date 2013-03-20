package com.gempukku.minecraft.automation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class ComputerBlock extends Block {
    public ComputerBlock(int id, Material material) {
        super(id, material);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
}
