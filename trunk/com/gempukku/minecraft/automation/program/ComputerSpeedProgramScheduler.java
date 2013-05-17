package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

import java.util.HashMap;
import java.util.Map;

public class ComputerSpeedProgramScheduler implements ProgramScheduler {
    private Map<Integer, Integer> _consumedSpeeds = new HashMap<Integer, Integer>();

    @Override
    public void addRunningProgram(RunningProgram runningProgram) {
        _consumedSpeeds.put(runningProgram.getComputerData().getId(), 0);
    }

    @Override
    public void processRunningProgram(RunningProgram runningProgram) throws ExecutionException {
        final ServerComputerData computerData = runningProgram.getComputerData();
        final int computerId = computerData.getId();
        int speedConsumed = _consumedSpeeds.get(computerId);
        speedConsumed -= computerData.getSpeed();
        final MinecraftComputerExecutionContext executionContext = runningProgram.getExecutionContext();
        while (speedConsumed <= 0) {
            final ExecutionProgress executionProgress = executionContext.executeNext();
            if (executionContext.getStackTraceSize() > computerData.getMaxStackSize())
                throw new ExecutionException(-1, "StackOverflow");
            speedConsumed += executionProgress.getCost();
            if (executionContext.isFinished() || executionContext.isSuspended())
                return;
        }
        _consumedSpeeds.put(computerId, speedConsumed);
    }

    @Override
    public void removeRunningProgram(RunningProgram runningProgram) {
        _consumedSpeeds.remove(runningProgram.getComputerData().getId());
    }
}
