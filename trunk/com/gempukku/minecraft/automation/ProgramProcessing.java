package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.ComputerExecutionContext;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProgramProcessing {
    private File _configFolder;
    private AutomationRegistry _registry;
    private ScriptParser _scriptParser;
    private Map<Integer, RunningProgram> _runningPrograms = new HashMap<Integer, RunningProgram>();

    public ProgramProcessing(File configFolder, AutomationRegistry registry) {
        _configFolder = configFolder;
        _registry = registry;
        _scriptParser = new ScriptParser();
    }

    public String startProgram(int computerId, String name) {
        if (_runningPrograms.containsKey(computerId))
            return "Computer already runs a program.";
        
        final File computerProgram = getComputerProgram(computerId, name);
        if (computerProgram == null)
            return "Cannot find program " + name + ".";

        final ComputerData computerData = _registry.getComputerData(computerId);
        try {
            ScriptExecutable parsedScript = parseScript(computerProgram);
            if (parsedScript == null)
                return "Unable to start a program, due to server error. Please contact server administrator.";

            ExecutionContext exec = initExecutionContext(computerData);
            CallContext context = new CallContext(null, false, true);
            exec.stackExecutionGroup(context, parsedScript.createExecution(context));
            _runningPrograms.put(computerId, new RunningProgram(computerData, exec));

            return null;
        } catch (IllegalSyntaxException exp) {
            return "IllegalSyntaxException - " + exp.getMessage();
        }
    }

    public String stopProgram(int computerId) {
        if (!_runningPrograms.containsKey(computerId))
            return "Computer is not running any programs.";

        _runningPrograms.remove(computerId);
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

    public boolean isRunningProgram(int computerId) {
        return _runningPrograms.containsKey(computerId);
    }

    public void progressAllPrograms() {
        final Iterator<RunningProgram> iterator = _runningPrograms.values().iterator();
        while (iterator.hasNext()) {
            final RunningProgram program = iterator.next();
            program.progressProgram();
            if (!program.isRunning())
                iterator.remove();
        }
    }

    private ExecutionContext initExecutionContext(ComputerData computerData) {
        ExecutionContext executionContext = new ComputerExecutionContext(computerData);
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
