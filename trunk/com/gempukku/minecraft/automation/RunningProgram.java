package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;
import net.minecraft.world.World;

public class RunningProgram {
    private static final int MEMORY_CHECK_INTERVAL = 10;
    private MinecraftComputerExecutionContext _executionContext;
    private ComputerData _computerData;
    private int _speedConsumed;
    private boolean _running = true;
    private int _memoryConsumptionCheck = 0;

    public RunningProgram(ComputerData computerData, MinecraftComputerExecutionContext executionContext) {
        _computerData = computerData;
        _executionContext = executionContext;
    }

    public void progressProgram(World world) {
        _executionContext.setWorld(world);
        _speedConsumed -= _computerData.getSpeed();
        while (_speedConsumed <= 0) {
            try {
                final ExecutionProgress executionProgress = _executionContext.executeNext();
                if (_executionContext.getStackTraceSize() > _computerData.getMaxStackSize())
                    throw new ExecutionException("StackOverflow");
                // Memory consumption calculation is expensive, so we will do it only from time to time
                if (++_memoryConsumptionCheck % MEMORY_CHECK_INTERVAL == 0 && _executionContext.getMemoryUsage() > _computerData.getMaxMemory())
                    throw new ExecutionException("OutOfMemory");
                _speedConsumed += executionProgress.getCost();
                if (_executionContext.isFinished()) {
                    _running = false;
                    return;
                }
            } catch (ExecutionException exp) {
                _computerData.appendToConsole("ExecutionException - " + exp.getMessage());
                _running = false;
            }
        }
    }

    public boolean isRunning() {
        return _running;
    }

    public ComputerData getComputerData() {
        return _computerData;
    }
}
