package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Automation {
    private File _modConfigDirectory;
    private Block _computerBlock;
//    private Item _computerItem;
    private int _computerBlockId;
//    private int _computerItemId;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _computerBlockId = conf.getBlock("computerBlock", 3624, "This is an ID of a computer block as it exists in the world").getInt();
//        _computerItemId = conf.getItem("computerItem", 2342, "This is an ID of a computer item as it exists in inventory or loose on the ground").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        _computerBlock = new ComputerBlock(_computerBlockId, Material.iron).setUnlocalizedName("computer");
//        _computerItem = new ComputerItemBlock(_computerItemId);

        GameRegistry.registerBlock(_computerBlock, ComputerItemBlock.class, "computer");

        LanguageRegistry.addName(_computerBlock, "Computer");
    }
}
