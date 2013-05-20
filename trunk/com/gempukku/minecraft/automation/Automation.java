package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.block.ComputerBlock;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.block.PersonalComputerBlock;
import com.gempukku.minecraft.automation.client.ClientAutomationPacketHandler;
import com.gempukku.minecraft.automation.client.ClientAutomationProxy;
import com.gempukku.minecraft.automation.computer.ComputerSpec;
import com.gempukku.minecraft.automation.computer.module.harvest.HarvestModule;
import com.gempukku.minecraft.automation.computer.module.mobility.MobilityModule;
import com.gempukku.minecraft.automation.computer.module.positioning.ComputerModuleItem;
import com.gempukku.minecraft.automation.computer.module.positioning.PositioningModule;
import com.gempukku.minecraft.automation.computer.module.redstone.RedstoneModule;
import com.gempukku.minecraft.automation.computer.module.storage.StorageModule;
import com.gempukku.minecraft.automation.gui.computer.ComputerGuiHandler;
import com.gempukku.minecraft.automation.item.ComputerItemBlock;
import com.gempukku.minecraft.automation.item.ItemTerminal;
import com.gempukku.minecraft.automation.program.ComputerSpeedProgramScheduler;
import com.gempukku.minecraft.automation.program.ProgramScheduler;
import com.gempukku.minecraft.automation.server.ServerAutomationPacketHandler;
import com.gempukku.minecraft.automation.server.ServerAutomationProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@Mod(modid = "MarcinSc_Automation", name = "Automation", version = "0.0")
@NetworkMod(clientSideRequired = true,
        clientPacketHandlerSpec = @NetworkMod.SidedPacketHandler(
                channels = {Automation.UPDATE_COMPUTER_LABEL, Automation.CLEAR_CONSOLE_SCREEN,
                        Automation.SET_CONSOLE_STATE, Automation.SET_CHARACTERS_IN_CONSOLE, Automation.APPEND_LINES_TO_CONSOLE,
                        Automation.PROGRAM_TEXT, Automation.DISPLAY_EXECUTION_RESULT, Automation.DISPLAY_LIST_OF_PROGRAMS},
                packetHandler = ClientAutomationPacketHandler.class),
        serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(
                channels = {Automation.UPDATE_COMPUTER_LABEL,
                        Automation.DOWNLOAD_PROGRAM, Automation.SAVE_PROGRAM,
                        Automation.INIT_CONSOLE, Automation.EXECUTE_PROGRAM, Automation.LIST_PROGRAMS},
                packetHandler = ServerAutomationPacketHandler.class))
public class Automation {
    private static final String AUTOMATION_CHANNEL_PREFIX = "automation.";
    public static final String UPDATE_COMPUTER_LABEL = AUTOMATION_CHANNEL_PREFIX + "2";
    public static final String CLEAR_CONSOLE_SCREEN = AUTOMATION_CHANNEL_PREFIX + "3";
    public static final String SET_CONSOLE_STATE = AUTOMATION_CHANNEL_PREFIX + "4";
    public static final String SET_CHARACTERS_IN_CONSOLE = AUTOMATION_CHANNEL_PREFIX + "5";
    public static final String APPEND_LINES_TO_CONSOLE = AUTOMATION_CHANNEL_PREFIX + "6";
    public static final String DOWNLOAD_PROGRAM = AUTOMATION_CHANNEL_PREFIX + "7";
    public static final String PROGRAM_TEXT = AUTOMATION_CHANNEL_PREFIX + "8";
    public static final String SAVE_PROGRAM = AUTOMATION_CHANNEL_PREFIX + "9";
    public static final String INIT_CONSOLE = AUTOMATION_CHANNEL_PREFIX + "10";
    public static final String EXECUTE_PROGRAM = AUTOMATION_CHANNEL_PREFIX + "11";
    public static final String DISPLAY_EXECUTION_RESULT = AUTOMATION_CHANNEL_PREFIX + "12";
    public static final String LIST_PROGRAMS = AUTOMATION_CHANNEL_PREFIX + "13";
    public static final String DISPLAY_LIST_OF_PROGRAMS = AUTOMATION_CHANNEL_PREFIX + "14";

    @Mod.Instance("MarcinSc_Automation")
    public static Automation instance;

    private static File _modConfigDirectory;
    private static ProgramScheduler _programScheduler;

    public static ComputerBlock personalComputerBlock;
    private static int _personalComputerBlockId;
    public static ComputerBlock smallComputerBlock;
    private static int _smallComputerBlockId;
    public static ComputerBlock advancedComputerBlock;
    private static int _advancedComputerBlockId;
    public static ComputerBlock industrialComputerBlock;
    private static int _industrialComputerBlockId;
    public static ComputerBlock superComputerBlock;
    private static int _superComputerBlockId;

    public static Item terminalItem;
    private static int _terminalItemId;

    public static Item moduleItem;
    private static int _moduleItemId;

    public static final int POSITIONING_MODULE_METADATA = 0;
    public static final int STORAGE_MODULE_METADATA = 1;
    public static final int MOBILITY_MODULE_METADATA = 2;
    public static final int HARVEST_MODULE_METADATA = 3;
    public static final int REDSTONE_MODULE_METADATA = 4;

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

        _personalComputerBlockId = conf.getBlock("personalComputerBlock", 3624, "This is an ID of a personal computer block").getInt();
        _smallComputerBlockId = conf.getBlock("smallComputerBlock", 3625, "This is an ID of a small computer block").getInt();
        _advancedComputerBlockId = conf.getBlock("advancedComputerBlock", 3626, "This is an ID of a advanced computer block").getInt();
        _industrialComputerBlockId = conf.getBlock("industrialComputerBlock", 3627, "This is an ID of a industrial computer block").getInt();
        _superComputerBlockId = conf.getBlock("superComputerBlock", 3628, "This is an ID of a super computer block").getInt();

        _moduleItemId = conf.getItem("computerModule", 4124, "This is an ID of a computer module item").getInt();
        _terminalItemId = conf.getItem("keyboard", 4125, "This is an ID of a keyboard item").getInt();

        final ComputerSpeedProgramScheduler programScheduler = new ComputerSpeedProgramScheduler();
        programScheduler.setComputerTypeSpeed("personal", 100);
        programScheduler.setComputerTypeSpeed("small", 200);
        programScheduler.setComputerTypeSpeed("advanced", 400);
        programScheduler.setComputerTypeSpeed("industrial", 800);
        programScheduler.setComputerTypeSpeed("super", 1200);
        _programScheduler = programScheduler;
    }

    @Mod.Init
    public void initialize(FMLInitializationEvent evt) {
        personalComputerBlock = new PersonalComputerBlock(_personalComputerBlockId, "personal");
        smallComputerBlock = new PersonalComputerBlock(_smallComputerBlockId, "small");
        advancedComputerBlock = new PersonalComputerBlock(_advancedComputerBlockId, "advanced");
        industrialComputerBlock = new PersonalComputerBlock(_industrialComputerBlockId, "industrial");
        superComputerBlock = new PersonalComputerBlock(_superComputerBlockId, "super");

        moduleItem = new ComputerModuleItem(_moduleItemId);
        terminalItem = new ItemTerminal(_terminalItemId);

        GameRegistry.registerTileEntity(ComputerTileEntity.class, "computerTileEntity");
        GameRegistry.registerBlock(personalComputerBlock, ComputerItemBlock.class, "personalComputer");
        GameRegistry.registerItem(moduleItem, "computerModule");
        GameRegistry.registerItem(terminalItem, "terminal");

        final ItemStack ironIngot = new ItemStack(Item.ingotIron);
        final ItemStack redstone = new ItemStack(Item.redstone);
        final ItemStack glassPane = new ItemStack(Block.thinGlass);
        final ItemStack woodenButton = new ItemStack(Block.woodenButton);
        final ItemStack coal = new ItemStack(Item.coal);
        final ItemStack chest = new ItemStack(Block.chest);
        final ItemStack goldIngot = new ItemStack(Item.ingotGold);
        final ItemStack compass = new ItemStack(Item.compass);
        final ItemStack netherQuartz = new ItemStack(Item.netherQuartz);
        final ItemStack diamond = new ItemStack(Item.diamond);

        final ItemStack diamondPickaxe = new ItemStack(Item.pickaxeDiamond);
        final ItemStack diamondAxe = new ItemStack(Item.axeDiamond);
        final ItemStack diamondShovel = new ItemStack(Item.shovelDiamond);

        GameRegistry.addShapedRecipe(new ItemStack(personalComputerBlock), "iri", "iii", 'i', ironIngot, 'r', redstone);
        GameRegistry.addShapedRecipe(new ItemStack(smallComputerBlock), "iri", "iqi", 'i', ironIngot, 'r', redstone, 'q', netherQuartz);
        GameRegistry.addShapedRecipe(new ItemStack(advancedComputerBlock), "iri", "ggg", " q ", 'i', ironIngot, 'r', redstone, 'q', netherQuartz, 'g', goldIngot);
        GameRegistry.addShapedRecipe(new ItemStack(industrialComputerBlock), "iri", "gdg", "iqi", 'i', ironIngot, 'r', redstone, 'q', netherQuartz, 'g', goldIngot, 'd', diamond);
        GameRegistry.addShapedRecipe(new ItemStack(superComputerBlock), "iri", "ddd", "iqi", 'i', ironIngot, 'r', redstone, 'q', netherQuartz, 'd', diamond);

        GameRegistry.addShapedRecipe(new ItemStack(terminalItem), "xyx", "zzz", 'x', redstone, 'y', glassPane, 'z', woodenButton);
        GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, POSITIONING_MODULE_METADATA), " x ", " x ", "yzy", 'x', coal, 'y', ironIngot, 'z', compass);
        GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, STORAGE_MODULE_METADATA), "xyx", "xzx", 'x', ironIngot, 'y', chest, 'z', redstone);
        GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, MOBILITY_MODULE_METADATA), "xxx", "yzy", "xxx", 'x', ironIngot, 'y', redstone, 'z', goldIngot);
        GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, HARVEST_MODULE_METADATA), "xyz", "iri", "iri", 'x', diamondShovel, 'y', diamondPickaxe, 'z', diamondAxe, 'i', ironIngot, 'r', redstone);
        GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, REDSTONE_MODULE_METADATA), "xyx", "yxy", "xxx", 'x', ironIngot, 'y', redstone);

        LanguageRegistry.addName(personalComputerBlock, "Personal Computer");
        LanguageRegistry.addName(terminalItem, "Terminal");

        proxy.initialize(_modConfigDirectory, _programScheduler);
        MinecraftForge.EVENT_BUS.register(proxy);

        NetworkRegistry.instance().registerGuiHandler(instance, new ComputerGuiHandler());
    }

    @Mod.PostInit
    public void postInitialize(FMLPostInitializationEvent evt) {
        proxy.getRegistry().registerComputerSpec(personalComputerBlock, new ComputerSpec("personal", 10, 10 * 1024));
        proxy.getRegistry().registerComputerSpec(smallComputerBlock, new ComputerSpec("small", 20, 20 * 1024));
        proxy.getRegistry().registerComputerSpec(advancedComputerBlock, new ComputerSpec("advanced", 40, 40 * 1024));
        proxy.getRegistry().registerComputerSpec(industrialComputerBlock, new ComputerSpec("industrial", 80, 80 * 1024));
        proxy.getRegistry().registerComputerSpec(superComputerBlock, new ComputerSpec("super", 160, 160 * 1024));

        proxy.getRegistry().registerComputerModule(moduleItem, POSITIONING_MODULE_METADATA, new PositioningModule());
        proxy.getRegistry().registerComputerModule(moduleItem, STORAGE_MODULE_METADATA, new StorageModule());
        proxy.getRegistry().registerComputerModule(moduleItem, MOBILITY_MODULE_METADATA, new MobilityModule());
        proxy.getRegistry().registerComputerModule(moduleItem, HARVEST_MODULE_METADATA, new HarvestModule());
        proxy.getRegistry().registerComputerModule(moduleItem, REDSTONE_MODULE_METADATA, new RedstoneModule());
    }

    @Mod.ServerAboutToStart
    public void initializeServerProxyIfNeeded(FMLServerAboutToStartEvent event) {
        // Initialize server Proxy (and all server-side objects)
        // This is needed to be able to initialize server objects in single-player mode
        Automation.getServerProxy();
    }

    @Mod.ServerStarted
    public void initializeRegistry(FMLServerStartedEvent event) {
        Automation.getServerProxy().getRegistry().initializeServer();
    }

    @Mod.ServerStopped
    public void removeLoadedComputers(FMLServerStoppedEvent event) {
        // We need to unload all loaded computers, as onChunkUnload is not called on TileEntities, when server is stopping
        // in single-player mode
        Automation.getServerProxy().getRegistry().closeDownServer();
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
        proxy.initialize(_modConfigDirectory, _programScheduler);
        return proxy;
    }

    private static ServerAutomationProxy createServerProxy() {
        ServerAutomationProxy proxy = new ServerAutomationProxy();
        proxy.initialize(_modConfigDirectory, _programScheduler);
        return proxy;
    }
}
