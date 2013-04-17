package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.block.ComputerBlock;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.block.SmallComputerBlock;
import com.gempukku.minecraft.automation.client.ClientAutomationPacketHandler;
import com.gempukku.minecraft.automation.client.ClientAutomationProxy;
import com.gempukku.minecraft.automation.computer.ComputerSpec;
import com.gempukku.minecraft.automation.gui.ComputerGuiHandler;
import com.gempukku.minecraft.automation.item.ComputerItemBlock;
import com.gempukku.minecraft.automation.item.ItemTerminal;
import com.gempukku.minecraft.automation.module.gps.ComputerModuleItem;
import com.gempukku.minecraft.automation.module.gps.GPSModule;
import com.gempukku.minecraft.automation.module.mobility.MobilityModule;
import com.gempukku.minecraft.automation.module.storage.StorageModule;
import com.gempukku.minecraft.automation.program.TickComputers;
import com.gempukku.minecraft.automation.server.ServerAutomationPacketHandler;
import com.gempukku.minecraft.automation.server.ServerAutomationProxy;
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
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(channels = {Automation.UPDATE_COMPUTER_LABEL}, packetHandler = ServerAutomationPacketHandler.class))
public class Automation {
    private static final String AUTOMATION_CHANNEL_PREFIX = "automation.";
    public static final String CLIENT_INIT = AUTOMATION_CHANNEL_PREFIX + "1";
    public static final String UPDATE_COMPUTER_LABEL = AUTOMATION_CHANNEL_PREFIX + "2";

    @Mod.Instance("MarcinSc_Automation")
    public static Automation instance;

    private static File _modConfigDirectory;

    public static ComputerBlock smallComputerBlock;
    private static int _smallComputerBlockId;

    public static Item terminalItem;
    private static int _terminalItemId;

    public static Item moduleItem;
    private static int _moduleItemId;

    public static final int GPS_MODULE_METADATA = 0;
    public static final int STORAGE_MODULE_METADATA = 1;
    public static final int MOBILITY_MODULE_METADATA = 2;

    @SidedProxy(clientSide = "com.gempukku.minecraft.automation.client.ClientAutomationProxy",
            serverSide = "com.gempukku.minecraft.automation.server.ServerAutomationProxy")
    public static AutomationProxy proxy;

    private static ServerAutomationProxy _serverProxy;
    private static ClientAutomationProxy _clientProxy;

    @Mod.PreInit
    public void preInitialize(FMLPreInitializationEvent evt) {
        Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
        conf.load();
        _modConfigDirectory = evt.getModConfigurationDirectory();
        _smallComputerBlockId = conf.getBlock("smallComputerBlock", 3624, "This is an ID of a computer block").getInt();
        _moduleItemId = conf.getItem("computerModule", 4124, "This is an ID of a computer module item").getInt();
        _terminalItemId = conf.getItem("keyboard", 4125, "This is an ID of a keyboard item").getInt();
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        smallComputerBlock = new SmallComputerBlock(_smallComputerBlockId, "small");

        moduleItem = new ComputerModuleItem(_moduleItemId);
        terminalItem = new ItemTerminal(_terminalItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(smallComputerBlock, "smallComputer");
        GameRegistry.registerItem(moduleItem, "computerModule");
        GameRegistry.registerItem(terminalItem, "terminal");
        GameRegistry.registerItem(new ComputerItemBlock(_smallComputerBlockId-256, smallComputerBlock), "smallComputerItem");

        LanguageRegistry.addName(smallComputerBlock, "Computer");
        LanguageRegistry.addName(terminalItem, "Terminal");

        TickRegistry.registerTickHandler(
                new TickComputers(), Side.SERVER);

        proxy.initialize(_modConfigDirectory);

        NetworkRegistry.instance().registerGuiHandler(instance, new ComputerGuiHandler());
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
        proxy.getRegistry().registerComputerSpec(smallComputerBlock, new ComputerSpec("small", 100, 100 * 1024, 100));
        
        proxy.getRegistry().registerComputerModule(moduleItem, GPS_MODULE_METADATA, new GPSModule());
        proxy.getRegistry().registerComputerModule(moduleItem, STORAGE_MODULE_METADATA, new StorageModule());
        proxy.getRegistry().registerComputerModule(moduleItem, MOBILITY_MODULE_METADATA, new MobilityModule());
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
