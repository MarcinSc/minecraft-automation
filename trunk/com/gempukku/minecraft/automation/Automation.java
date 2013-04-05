package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.gps.GPSModule;
import com.gempukku.minecraft.automation.module.gps.GpsModuleItem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Automation {
    private static AutomationRegistry _registry;
    private static ProgramProcessing _programProcessing;
    private static File _modConfigDirectory;

    public static Block _computerBlock;
    private static int _computerBlockId;

    public static Item _gpsModuleItem;
    private static int _gpsModuleItemId;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _computerBlockId = conf.getBlock("computerBlock", 3624, "This is an ID of a computer block").getInt();
        _gpsModuleItemId = conf.getItem("gpsModule", 3625, "This is an ID of a gps module item").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        _registry = new AutomationRegistry(_modConfigDirectory);
        _programProcessing = new ProgramProcessing(_modConfigDirectory, _registry);

        _computerBlock = new ComputerBlock(_computerBlockId);

        _gpsModuleItem = new GpsModuleItem(_gpsModuleItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(_computerBlock, ComputerItemBlock.class, "computer");

        LanguageRegistry.addName(_computerBlock, "Computer");
        LanguageRegistry.addName(_gpsModuleItem, "GPS module");
        
        TickRegistry.registerTickHandler(
                new ProcessRunningPrograms(), Side.SERVER);
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
        getRegistry().registerComputerModule(_gpsModuleItem, new GPSModule());
    }

    public static AutomationRegistry getRegistry() {
        return _registry;
    }

    public static ProgramProcessing getProgramProcessing() {
        return _programProcessing;
    }
}
