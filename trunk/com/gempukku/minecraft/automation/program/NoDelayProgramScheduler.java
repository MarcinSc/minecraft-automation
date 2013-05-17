package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

import java.util.HashMap;
import java.util.Map;

public class NoDelayProgramScheduler implements ProgramScheduler {
    private Map<Integer, Integer> _ticksToSkip = new HashMap<Integer, Integer>();

    @Override
    public void addRunningProgram(RunningProgram runningProgram) {
        _ticksToSkip.put(runningProgram.getComputerData().getId(), 0);
    }

    @Override
    public void processRunningProgram(RunningProgram runningProgram) throws ExecutionException {
        final ServerComputerData computerData = runningProgram.getComputerData();
        final int computerId = computerData.getId();
        final Integer ticksToSkip = _ticksToSkip.get(computerId);
        if (ticksToSkip > 0) {
            _ticksToSkip.put(computerId, ticksToSkip - 1);
        } else {
            final MinecraftComputerExecutionContext executionContext = runningProgram.getExecutionContext();
            while (true) {
                final ExecutionProgress executionProgress = executionContext.executeNext();
                if (executionContext.getStackTraceSize() > computerData.getMaxStackSize())
                    throw new ExecutionException(-1, "StackOverflow");
                final int minExecutionTicks = executionProgress.getMinExecutionTicks();
                if (minExecutionTicks > 0) {
                    _ticksToSkip.put(computerId, minExecutionTicks - 1);
                    return;
                }
                if (executionContext.isFinished() || executionContext.isSuspended())
                    return;
            }
        }
    }

    @Override
    public void removeRunningProgram(RunningProgram runningProgram) {
        _ticksToSkip.put(runningProgram.getComputerData().getId(), 0);
    }
}
