package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.block.ComputerBlock;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.block.PersonalComputerBlock;
import com.gempukku.minecraft.automation.client.ClientAutomationPacketHandler;
import com.gempukku.minecraft.automation.client.ClientAutomationProxy;
import com.gempukku.minecraft.automation.computer.ComputerSpec;
import com.gempukku.minecraft.automation.gui.computer.ComputerGuiHandler;
import com.gempukku.minecraft.automation.item.ComputerItemBlock;
import com.gempukku.minecraft.automation.item.ItemTerminal;
import com.gempukku.minecraft.automation.module.gps.ComputerModuleItem;
import com.gempukku.minecraft.automation.module.gps.PositioningModule;
import com.gempukku.minecraft.automation.module.harvest.HarvestModule;
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
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
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
												Automation.PROGRAM_TEXT, Automation.DISPLAY_EXECUTION_RESULT},
								packetHandler = ClientAutomationPacketHandler.class),
				serverPacketHandlerSpec = @NetworkMod.SidedPacketHandler(
								channels = {Automation.UPDATE_COMPUTER_LABEL,
												Automation.DOWNLOAD_PROGRAM, Automation.SAVE_PROGRAM,
												Automation.INIT_CONSOLE, Automation.EXECUTE_PROGRAM},
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

	@Mod.Instance("MarcinSc_Automation")
	public static Automation instance;

	private static File _modConfigDirectory;

	public static ComputerBlock personalComputerBlock;
	private static int _smallComputerBlockId;

	public static Item terminalItem;
	private static int _terminalItemId;

	public static Item moduleItem;
	private static int _moduleItemId;

	public static final int POSITIONING_MODULE_METADATA = 0;
	public static final int STORAGE_MODULE_METADATA = 1;
	public static final int MOBILITY_MODULE_METADATA = 2;
	public static final int HARVEST_MODULE_METADATA = 3;

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
		personalComputerBlock = new PersonalComputerBlock(_smallComputerBlockId, "personal");

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

		final ItemStack diamondPickaxe = new ItemStack(Item.pickaxeDiamond);
		final ItemStack diamondAxe = new ItemStack(Item.axeDiamond);
		final ItemStack diamondShovel = new ItemStack(Item.shovelDiamond);

		GameRegistry.addShapedRecipe(new ItemStack(personalComputerBlock), "xyx", "xxx", 'x', ironIngot, 'y', redstone);
		GameRegistry.addShapedRecipe(new ItemStack(terminalItem), "xyx", "zzz", 'x', redstone, 'y', glassPane, 'z', woodenButton);
		GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, POSITIONING_MODULE_METADATA), " x ", " x ", "yzy", 'x', coal, 'y', ironIngot, 'z', compass);
		GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, STORAGE_MODULE_METADATA), "xyx", "xzx", 'x', ironIngot, 'y', chest, 'z', redstone);
		GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, MOBILITY_MODULE_METADATA), "xxx", "yzy", "xxx", 'x', ironIngot, 'y', redstone, 'z', goldIngot);
		GameRegistry.addShapedRecipe(new ItemStack(moduleItem, 1, HARVEST_MODULE_METADATA), "xyz", "iri", "iri", 'x', diamondShovel, 'y', diamondPickaxe, 'z', diamondAxe, 'i', ironIngot, 'r', redstone);

		LanguageRegistry.addName(personalComputerBlock, "Personal Computer");
		LanguageRegistry.addName(terminalItem, "Terminal");

		TickRegistry.registerTickHandler(
						new TickComputers(), Side.SERVER);

		proxy.initialize(_modConfigDirectory);
		MinecraftForge.EVENT_BUS.register(proxy);

		NetworkRegistry.instance().registerGuiHandler(instance, new ComputerGuiHandler());
	}

	@Mod.PostInit
	public void postInitialize(FMLPostInitializationEvent evt) {
		proxy.getRegistry().registerComputerSpec(personalComputerBlock, new ComputerSpec("personal", 100, 100 * 1024, 100));

		proxy.getRegistry().registerComputerModule(moduleItem, POSITIONING_MODULE_METADATA, new PositioningModule());
		proxy.getRegistry().registerComputerModule(moduleItem, STORAGE_MODULE_METADATA, new StorageModule());
		proxy.getRegistry().registerComputerModule(moduleItem, MOBILITY_MODULE_METADATA, new MobilityModule());
		proxy.getRegistry().registerComputerModule(moduleItem, HARVEST_MODULE_METADATA, new HarvestModule());
	}

	@Mod.ServerAboutToStart
	public void initializeServerProxyIfNeeded(FMLServerAboutToStartEvent event) {
		// Initialize server Proxy (and all server-side objects)
		// This is needed to be able to initialize server objects in single-player mode
		Automation.getServerProxy();
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
