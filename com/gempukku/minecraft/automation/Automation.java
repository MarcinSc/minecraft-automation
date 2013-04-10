package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.gps.GpsModuleItem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ClientAutomationPacketHandler.class),
        serverSideRequired = true,
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ServerAutomationPacketHandler.class))
public class Automation {
    private static final String AUTOMATION_CHANNEL_PREFIX = "atm.";
    public static final String UPDATE_COMPUTER_LABEL = AUTOMATION_CHANNEL_PREFIX + "updCompLabel";

    @Mod.Instance("Tiny")
    public static Automation _instance;
    private static AutomationRegistry _registry;
    private static ProgramProcessing _programProcessing;

    private static File _modConfigDirectory;

    public static ComputerBlock _computerBlock;
    private static int _computerBlockId;

    public static Item _keyboardItem;
    private static int _keyboardItemId;

    public static Item _gpsModuleItem;
    private static int _gpsModuleItemId;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        conf.load();
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _computerBlockId = conf.getBlock("computerBlock", 3624, "This is an ID of a computer block").getInt();
        _gpsModuleItemId = conf.getItem("gpsModule", 3625, "This is an ID of a gps module item").getInt();
        _keyboardItemId = conf.getItem("keyboard", 3626, "This is an ID of a keyboard item").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        _computerBlock = new ComputerBlock(_computerBlockId);

        _gpsModuleItem = new GpsModuleItem(_gpsModuleItemId);
        _keyboardItem = new ItemKeyboard(_keyboardItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(_computerBlock, ComputerItemBlock.class, "computer");
        GameRegistry.registerItem(_gpsModuleItem, "gpsModule");

        LanguageRegistry.addName(_computerBlock, "Computer");
        LanguageRegistry.addName(_gpsModuleItem, "GPS module");
        LanguageRegistry.addName(_keyboardItem, "Keyboard");

        TickRegistry.registerTickHandler(
                new ProcessRunningPrograms(), Side.SERVER);

        _registry = new ServerAutomationRegistry(_modConfigDirectory);
        _programProcessing = new ProgramProcessing(_modConfigDirectory, _registry);

        MinecraftForge.EVENT_BUS.register(_registry);
        MinecraftForge.EVENT_BUS.register(_programProcessing);

        NetworkRegistry.instance().registerGuiHandler(this, new ComputerGuiHandler());
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
//        getRegistry().registerComputerModule(_gpsModuleItem, new GPSModule());
    }

    public static Automation getInstance() {
        return _instance;
    }

    public static AutomationRegistry getRegistry() {
        return _registry;
    }

    public static ProgramProcessing getProgramProcessing() {
        return _programProcessing;
    }
}
