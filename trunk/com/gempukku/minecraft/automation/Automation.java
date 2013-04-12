package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.gps.GPSModule;
import com.gempukku.minecraft.automation.module.gps.GpsModuleItem;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
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

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ClientAutomationPacketHandler.class),
        serverSideRequired = true,
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ServerAutomationPacketHandler.class))
public class Automation {
    private static final String AUTOMATION_CHANNEL_PREFIX = "atm.";
    public static final String UPDATE_COMPUTER_LABEL = AUTOMATION_CHANNEL_PREFIX + "updCompLabel";
    public static final String CLIENT_INIT = AUTOMATION_CHANNEL_PREFIX + "clientInit";

    @Mod.Instance("Tiny")
    public static Automation instance;

    private static File _modConfigDirectory;

    public static ComputerBlock computerBlock;
    private static int _computerBlockId;

    public static Item terminalItem;
    private static int _terminalItemId;

    public static Item gpsModuleItem;
    private static int _gpsModuleItemId;

    @SidedProxy(clientSide = "com.gempukku.minecraft.automation.ClientAutomationProxy",
            serverSide = "com.gempukku.minecraft.automation.ServerAutomationProxy")
    public static AutomationProxy proxy;

    private static ServerAutomationProxy _serverProxy;
    private static ClientAutomationProxy _clientProxy;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        conf.load();
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _computerBlockId = conf.getBlock("computerBlock", 3624, "This is an ID of a computer block").getInt();
        _gpsModuleItemId = conf.getItem("gpsModule", 3625, "This is an ID of a gps module item").getInt();
        _terminalItemId = conf.getItem("keyboard", 3626, "This is an ID of a keyboard item").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        computerBlock = new ComputerBlock(_computerBlockId);

        gpsModuleItem = new GpsModuleItem(_gpsModuleItemId);
        terminalItem = new ItemTerminal(_terminalItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(computerBlock, ComputerItemBlock.class, "computer");
        GameRegistry.registerItem(gpsModuleItem, "gpsModule");
        GameRegistry.registerItem(terminalItem, "terminal");

        LanguageRegistry.addName(computerBlock, "Computer");
        LanguageRegistry.addName(gpsModuleItem, "GPS module");
        LanguageRegistry.addName(terminalItem, "Terminal");

        TickRegistry.registerTickHandler(
                new ProcessRunningPrograms(), Side.SERVER);

        proxy.initialize(_modConfigDirectory);

        NetworkRegistry.instance().registerGuiHandler(this, new ComputerGuiHandler());
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
        proxy.getRegistry().registerComputerModule(gpsModuleItem, new GPSModule());
    }

    public static synchronized ServerAutomationProxy getServerProxy() {
        if (_serverProxy == null) {
            if (proxy instanceof ServerAutomationProxy)
                _serverProxy = (ServerAutomationProxy) proxy;
            else
                _serverProxy = createServerProxy();
        }
        return _serverProxy;
    }

    public static synchronized ClientAutomationProxy getClientProxy() {
        if (_clientProxy == null) {
            if (proxy instanceof ClientAutomationProxy)
                _clientProxy = (ClientAutomationProxy) proxy;
            else
                _clientProxy = createClientProxy();
        }
        return _clientProxy;
    }

    private static ClientAutomationProxy createClientProxy() {
        ClientAutomationProxy proxy = new ClientAutomationProxy();
        proxy.initialize(_modConfigDirectory);
        return proxy;
    }

    private static ServerAutomationProxy createServerProxy() {
        ServerAutomationProxy proxy = new ServerAutomationProxy();
        proxy.initialize(_modConfigDirectory);
        return proxy;
    }
}
