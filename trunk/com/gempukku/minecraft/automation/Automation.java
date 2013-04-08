package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.gps.GpsModuleItem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ClientAutomationPacketHandler.class),
        serverSideRequired = true,
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ServerAutomationPacketHandler.class))
public class Automation {
    private static final String AUTOMATION_CHANNEL_PREFIX = "atm.";
    public static final String UPDATE_COMPUTER_LABEL = AUTOMATION_CHANNEL_PREFIX + "updCompLabel";

    private static File _modConfigDirectory;

    public static ComputerBlock _computerBlock;
    private static int _computerBlockId;

    public static Item _gpsModuleItem;
    private static int _gpsModuleItemId;

    @SidedProxy(clientSide = "com.gempukku.minecraft.automation.AutomationOnClient",
            serverSide = "com.gempukku.minecraft.automation.AutomationOnServer")
    private static AutomationProxy _automationProxy;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        conf.load();
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _computerBlockId = conf.getBlock("computerBlock", 3624, "This is an ID of a computer block").getInt();
        _gpsModuleItemId = conf.getItem("gpsModule", 3625, "This is an ID of a gps module item").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        _computerBlock = new ComputerBlock(_computerBlockId);

        _gpsModuleItem = new GpsModuleItem(_gpsModuleItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(_computerBlock, ComputerItemBlock.class, "computer");
        GameRegistry.registerItem(_gpsModuleItem, "gpsModule");

        LanguageRegistry.addName(_computerBlock, "Computer");
        LanguageRegistry.addName(_gpsModuleItem, "GPS module");

        TickRegistry.registerTickHandler(
                new ProcessRunningPrograms(), Side.SERVER);

        _automationProxy.initialize(_modConfigDirectory);
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
//        getRegistry().registerComputerModule(_gpsModuleItem, new GPSModule());
    }

    public static AutomationRegistry getRegistry() {
        return _automationProxy.getAutomationRegistry();
    }

    public static ProgramProcessing getProgramProcessing() {
        return _automationProxy.getProgramProcessing();
    }
}
