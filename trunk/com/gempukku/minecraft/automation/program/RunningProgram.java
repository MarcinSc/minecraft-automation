package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import net.minecraft.world.World;

public class RunningProgram {
    private static final int MEMORY_CHECK_INTERVAL = 10;
    private MinecraftComputerExecutionContext _executionContext;
    private ServerComputerData _computerData;
    private boolean _running = true;
    private int _memoryConsumptionCheck = 0;

    public RunningProgram(ServerComputerData computerData, MinecraftComputerExecutionContext executionContext) {
        _computerData = computerData;
        _executionContext = executionContext;
    }

    public MinecraftComputerExecutionContext getExecutionContext() {
        return _executionContext;
    }

    public void progressProgram(World world, ProgramScheduler programScheduler) {
        _executionContext.setWorld(world);
        try {
            try {
                programScheduler.processRunningProgram(this);
                // Memory consumption calculation is expensive, so we will do it only from time to time
                if (++_memoryConsumptionCheck % MEMORY_CHECK_INTERVAL == 0 && _executionContext.getMemoryUsage() > _computerData.getMaxMemory())
                    throw new ExecutionException(-1, "OutOfMemory");
            } catch (ExecutionException exp) {
                if (exp.getLine() == -1)
                    _computerData.appendToConsole("ExecutionException[unknown line] - " + exp.getMessage());
                else
                    _computerData.appendToConsole("ExecutionException[line " + exp.getLine() + "] - " + exp.getMessage());
                _running = false;
            }
        } finally {
            _executionContext.setWorld(null);
        }
    }

    public boolean isFinished() {
        return _executionContext.isFinished();
    }

    public boolean isSuspended() {
        return _executionContext.isSuspended();
    }

    public ServerComputerData getComputerData() {
        return _computerData;
    }
}
