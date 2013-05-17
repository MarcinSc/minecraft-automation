package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.computer.computer.ComputerObjectDefinition;
import com.gempukku.minecraft.automation.computer.console.ConsoleObjectDefinition;
import com.gempukku.minecraft.automation.computer.os.OSObjectDefinition;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import com.gempukku.minecraft.automation.server.ServerAutomationRegistry;
import net.minecraft.world.World;
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
    private ProgramScheduler _programScheduler;
    private ScriptParser _scriptParser;
    private Map<Integer, RunningProgram> _runningPrograms = new HashMap<Integer, RunningProgram>();
    private Map<Integer, SuspendedProgram> _suspendedPrograms = new HashMap<Integer, SuspendedProgram>();
    private Set<String> _predefinedVariables = new HashSet<String>();

    public ComputerProcessing(File savesFolder, ServerAutomationRegistry registry, ProgramScheduler programScheduler) {
        _savesFolder = savesFolder;
        _registry = registry;
        _programScheduler = programScheduler;
        _scriptParser = new ScriptParser();
        _predefinedVariables.add("os");
        _predefinedVariables.add("console");
        _predefinedVariables.add("computer");
    }

    public boolean isRunningProgram(int computerId) {
        return _runningPrograms.containsKey(computerId);
    }

    @ForgeSubscribe
    public void startupComputer(ComputerEvent.ComputerAddedToWorldEvent evt) {
        final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(evt.computerId);
        final ComputerConsole computerConsole = computerData.getConsole();
        computerConsole.appendString("Staring startup program");
        String startupProgramResult = startProgram(computerData.getId(), STARTUP_PROGRAM);
        if (startupProgramResult != null)
            computerConsole.appendString(startupProgramResult);
    }

    @ForgeSubscribe
    public void shutdownComputer(ComputerEvent.ComputerRemovedFromWorldEvent evt) {
        removeRunningProgram(evt.computerId);
    }

    private void addRunningProgram(int computerId, RunningProgram runningProgram) {
        _runningPrograms.put(computerId, runningProgram);
        _programScheduler.addRunningProgram(runningProgram);
    }

    private void removeRunningProgram(int computerId) {
        final RunningProgram runningProgram = _runningPrograms.remove(computerId);
        if (runningProgram != null) {
            _programScheduler.removeRunningProgram(runningProgram);
            _suspendedPrograms.remove(computerId);
        }
    }

    public String startProgram(int computerId, String name) {
        if (isRunningProgram(computerId))
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
            try {
                context.defineVariable("os").setValue(new OSObjectDefinition());
                context.defineVariable("console").setValue(new ConsoleObjectDefinition());
                context.defineVariable("computer").setValue(new ComputerObjectDefinition());
            } catch (ExecutionException exp) {
                // Ignore
            }
            exec.stackExecutionGroup(context, parsedScript.createExecution(context));
            addRunningProgram(computerId, new RunningProgram(computerData, exec));

            return null;
        } catch (IllegalSyntaxException exp) {
            return "IllegalSyntaxException - " + exp.getMessage();
        }
    }

    public void suspendProgramWithCondition(ComputerTileEntity computerEntity, AwaitingCondition condition) {
        int computerId = computerEntity.getComputerId();
        final RunningProgram runningProgram = _runningPrograms.get(computerId);
        _suspendedPrograms.put(computerId, new SuspendedProgram(runningProgram, condition));
    }

    public String stopProgram(World world, int computerId) {
        if (!isRunningProgram(computerId))
            return "Computer is not running any programs.";

        removeRunningProgram(computerId);
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

    public void processProgram(ComputerTileEntity computerEntity) {
        final int computerId = computerEntity.getComputerId();
        final RunningProgram runningProgram = _runningPrograms.get(computerId);
        if (runningProgram != null)
            processRunningProgram(computerEntity, computerId, runningProgram);
        else
            updateProgramState(computerEntity, ComputerTileEntity.STATE_IDLE);
    }

    private void processSuspendedProgram(ComputerTileEntity computerEntity, int computerId, SuspendedProgram suspendedProgram) {
        final ServerComputerData computerData = suspendedProgram.getRunningProgram().getComputerData();
        computerData.setTileEntity(computerEntity);
        try {
            final World world = computerEntity.worldObj;
            try {
                if (suspendedProgram.getAwaitingCondition().isMet(suspendedProgram.getCheckAttempt(), world, computerData)) {
                    _suspendedPrograms.remove(computerId);
                    suspendedProgram.getRunningProgram().getExecutionContext().setSuspended(false);
                }
            } catch (ExecutionException exp) {
                if (exp.getLine() == -1)
                    computerData.appendToConsole("ExecutionException[unknown line] - " + exp.getMessage());
                else
                    computerData.appendToConsole("ExecutionException[line " + exp.getLine() + "] - " + exp.getMessage());
                _suspendedPrograms.remove(computerId);
            }
        } catch (Exception exp) {
            // TODO
        } finally {
            computerData.resetTileEntity();
        }
    }

    private void processRunningProgram(ComputerTileEntity computerEntity, int computerId, RunningProgram runningProgram) {
        if (runningProgram.isSuspended()) {
            processSuspendedProgram(computerEntity, computerId, _suspendedPrograms.get(computerId));
        } else {
            final ServerComputerData computerData = runningProgram.getComputerData();
            computerData.setTileEntity(computerEntity);
            try {
                final World world = computerEntity.worldObj;
                runningProgram.progressProgram(world, _programScheduler);
            } catch (Exception exp) {
                // TODO
            } finally {
                computerData.resetTileEntity();
            }
        }
        if (runningProgram.isFinished()) {
            removeRunningProgram(computerId);
        } else if (runningProgram.isSuspended()) {
            updateProgramState(computerEntity, ComputerTileEntity.STATE_SUSPENDED);
        } else
            updateProgramState(computerEntity, ComputerTileEntity.STATE_RUNNING);
    }

    private void updateProgramState(ComputerTileEntity computerTileEntity, short state) {
        if (computerTileEntity != null) {
            short oldState = computerTileEntity.getState();
            if (oldState != state) {
                computerTileEntity.setState(state);
                MinecraftUtils.sendTileEntityUpdateToPlayers(computerTileEntity.worldObj, computerTileEntity);
            }
        }
    }

    private MinecraftComputerExecutionContext initExecutionContext(ServerComputerData computerData) {
        MinecraftComputerExecutionContext executionContext = new MinecraftComputerExecutionContext(computerData);
        executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
        executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
        executionContext.addPropertyProducer(Variable.Type.LIST, new ListPropertyProducer());
        executionContext.addPropertyProducer(Variable.Type.STRING, new StringPropertyProducer());
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
                return _scriptParser.parseScript(reader, _predefinedVariables);
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
