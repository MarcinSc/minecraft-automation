package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import com.gempukku.minecraft.automation.server.ServerAutomationRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class is used on server only and controls processing of programs and computer ticks.
 */
public class ComputerProcessing {
	public static final String STARTUP_PROGRAM = "startup";
	private File _savesFolder;
	private ServerAutomationRegistry _registry;
	private ScriptParser _scriptParser;
	private Map<Integer, RunningProgram> _runningPrograms = new HashMap<Integer, RunningProgram>();

	public ComputerProcessing(File savesFolder, ServerAutomationRegistry registry) {
		_savesFolder = savesFolder;
		_registry = registry;
		_scriptParser = new ScriptParser();
	}

	@ForgeSubscribe
	public void startupComputer(ComputerEvent.ComputerAddedToWorldEvent evt) {
		final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(evt.getComputerTileEntity().getComputerId());
		final ComputerConsole computerConsole = computerData.getConsole();
		computerConsole.appendString("Staring startup program");
		String startupProgramResult = startProgram(computerData.getId(), STARTUP_PROGRAM);
		if (startupProgramResult != null)
			computerConsole.appendString(startupProgramResult);
	}

	@ForgeSubscribe
	public void shutdownComputer(ComputerEvent.ComputerRemovedFromWorldEvent evt) {
		_runningPrograms.remove(evt.getComputerTileEntity().getComputerId());
	}

	public String startProgram(int computerId, String name) {
		if (_runningPrograms.containsKey(computerId))
			return "Computer already runs a program.";

		final File computerProgram = getComputerProgram(computerId, name);
		if (computerProgram == null)
			return "Cannot find program " + name + ".";

		final ServerComputerData computerData = _registry.getComputerData(computerId);
		try {
			ScriptExecutable parsedScript = parseScript(computerProgram);
			if (parsedScript == null)
				return "Unable to start a program, due to server error. Please contact server administrator.";

			MinecraftComputerExecutionContext exec = initExecutionContext(computerData);
			CallContext context = new CallContext(null, false, true);
			exec.stackExecutionGroup(context, parsedScript.createExecution(context));
			_runningPrograms.put(computerId, new RunningProgram(computerData, exec));

			setProgramRunning(computerData, true);

			return null;
		} catch (IllegalSyntaxException exp) {
			return "IllegalSyntaxException - " + exp.getMessage();
		}
	}

	public String stopProgram(int computerId) {
		if (!_runningPrograms.containsKey(computerId))
			return "Computer is not running any programs.";

		final RunningProgram stoppedProgram = _runningPrograms.remove(computerId);
		if (stoppedProgram != null) {
			final ServerComputerData computerData = stoppedProgram.getComputerData();
			setProgramRunning(computerData, false);
		}
		return null;
	}

	public List<String> listPrograms(int computerId) {
		final File computerFolder = getComputerFolder(computerId);
		if (computerFolder == null)
			return null;
		final File[] files = computerFolder.listFiles();
		List<String> result = new ArrayList<String>(files.length);
		for (File file : files)
			result.add(file.getName());

		return result;
	}

	public String getProgram(int computerId, String programName) {
		File computerProgramFile = getComputerProgramFile(computerId, programName);
		if (computerProgramFile.exists() && computerProgramFile.isFile()) {
			return readFileContents(computerProgramFile);
		} else {
			return null;
		}
	}

	public void saveProgram(int id, String programName, String programText) {
		File computerProgramFile = getComputerProgramFile(id, programName);
		computerProgramFile.getParentFile().mkdirs();
		try {
			FileWriter writer = new FileWriter(computerProgramFile);
			try {
				writer.write(programText);
			} finally {
				writer.close();
			}
		} catch (IOException exp) {
			// TODO
		}
	}

	public void tickComputers() {
		final Iterator<RunningProgram> iterator = _runningPrograms.values().iterator();
		while (iterator.hasNext()) {
			final RunningProgram program = iterator.next();
			program.progressProgram();
			if (!program.isRunning()) {
				iterator.remove();
				final ServerComputerData computerData = program.getComputerData();
				setProgramRunning(computerData, false);
			}
		}
	}

	private void setProgramRunning(ServerComputerData computerData, boolean running) {
		ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(computerData);
		if (computerTileEntity != null) {
			computerTileEntity.setRunningProgram(running);
			MinecraftUtils.updateTileEntity(DimensionManager.getWorld(computerData.getDimension()), computerData.getX(), computerData.getY(), computerData.getZ());
		}
	}

	private MinecraftComputerExecutionContext initExecutionContext(ServerComputerData computerData) {
		MinecraftComputerExecutionContext executionContext = new MinecraftComputerExecutionContext(computerData);
		executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
		executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
		return executionContext;
	}

	private String readFileContents(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			FileReader reader = new FileReader(file);
			try {
				char[] chars = new char[1024];
				int cnt;
				while ((cnt = reader.read(chars)) != -1)
					sb.append(chars, 0, cnt);

				return sb.toString();
			} finally {
				reader.close();
			}
		} catch (IOException exp) {
			return null;
		}
	}

	private ScriptExecutable parseScript(File computerProgram) throws IllegalSyntaxException {
		try {
			FileReader reader = new FileReader(computerProgram);
			try {
				return _scriptParser.parseScript(reader);
			} finally {
				try {
					reader.close();
				} catch (Exception exp) {
					// Ignore
				}
			}
		} catch (IOException exp) {
			return null;
		}
	}

	private File getComputerProgram(int computerId, String name) {
		final File computerFolder = getComputerFolder(computerId);
		if (computerFolder == null)
			return null;
		File program = new File(computerFolder, name + ".ajs");
		if (program.exists() && program.isFile())
			return program;
		return null;
	}

	private File getComputerProgramFile(int computerId, String name) {
		return new File(AutomationUtils.getComputerSavesFolder(_savesFolder, computerId), name + ".ajs");
	}

	private File getComputerFolder(int computerId) {
		File computerFolder = AutomationUtils.getComputerSavesFolder(_savesFolder, computerId);
		if (computerFolder.exists() && computerFolder.isDirectory())
			return computerFolder;
		return null;
	}
}
