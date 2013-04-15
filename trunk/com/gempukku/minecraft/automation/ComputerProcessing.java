package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import com.gempukku.minecraft.automation.module.ComputerModule;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class is used on server only and controls processing of programs and computer ticks.
 */
public class ComputerProcessing {
    public static final String STARTUP_PROGRAM = "startup";
    private File _configFolder;
    private ServerAutomationRegistry _registry;
    private ScriptParser _scriptParser;
    private Set<Integer> _loadedComputerIds = new HashSet<Integer>();
    private Map<Integer, RunningProgram> _runningPrograms = new HashMap<Integer, RunningProgram>();

    public ComputerProcessing(File configFolder, ServerAutomationRegistry registry) {
        _configFolder = configFolder;
        _registry = registry;
        _scriptParser = new ScriptParser();
    }

    @ForgeSubscribe
    public void computerAddedToWorld(ComputerEvent.ComputerAddedToWorldEvent evt) {
        final int computerId = evt.getComputerTileEntity().getComputerId();
        startProgram(evt.getWorld(), computerId, STARTUP_PROGRAM);
        _loadedComputerIds.add(computerId);
    }

    @ForgeSubscribe
    public void computerRemovedFromWorld(ComputerEvent.ComputerRemovedFromWorldEvent evt) {
        final int computerId = evt.getComputerTileEntity().getComputerId();
        _runningPrograms.remove(computerId);
        _loadedComputerIds.remove(computerId);
    }

    public String startProgram(World world, int computerId, String name) {
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

            setProgramRunning(world, computerData, true);

            return null;
        } catch (IllegalSyntaxException exp) {
            return "IllegalSyntaxException - " + exp.getMessage();
        }
    }

    public String stopProgram(World world, int computerId) {
        if (!_runningPrograms.containsKey(computerId))
            return "Computer is not running any programs.";

        final RunningProgram stoppedProgram = _runningPrograms.remove(computerId);
        if (stoppedProgram != null) {
            final ServerComputerData computerData = stoppedProgram.getComputerData();
            setProgramRunning(world, computerData, false);
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

    @SideOnly(Side.SERVER)
    public void tickComputers(World world) {
        for (int computerId : _loadedComputerIds) {
            final ServerComputerData computerData = _registry.getComputerData(computerId);
            final int moduleSlotCount = computerData.getModuleSlotCount();
            for (int i = 0; i < moduleSlotCount; i++) {
                final ComputerModule module = computerData.getModuleAt(i);
                if (module != null)
                    module.onTick(world, computerData);
            }
        }

        final Iterator<RunningProgram> iterator = _runningPrograms.values().iterator();
        while (iterator.hasNext()) {
            final RunningProgram program = iterator.next();
            program.progressProgram(world);
            if (!program.isRunning()) {
                iterator.remove();
                final ServerComputerData computerData = program.getComputerData();
                setProgramRunning(world, computerData, false);
            }
        }
    }

    private void setProgramRunning(World world, ServerComputerData computerData, boolean running) {
        ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computerData.getX(), computerData.getY(), computerData.getZ());
        if (computerTileEntity != null) {
            computerTileEntity.setRunningProgram(running);
            MinecraftUtils.updateTileEntity(world, computerData.getX(), computerData.getY(), computerData.getZ(), computerTileEntity);
        }
    }

    private MinecraftComputerExecutionContext initExecutionContext(ServerComputerData computerData) {
        MinecraftComputerExecutionContext executionContext = new MinecraftComputerExecutionContext(computerData);
        executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
        executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
        return executionContext;
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
        File program = new File(computerFolder, name);
        if (program.exists() && program.isFile())
            return program;
        return null;
    }

    private File getComputerFolder(int computerId) {
        File computerFolder = new File(_configFolder, String.valueOf(computerId));
        if (computerFolder.exists() && computerFolder.isDirectory())
            return computerFolder;
        return null;
    }
}
