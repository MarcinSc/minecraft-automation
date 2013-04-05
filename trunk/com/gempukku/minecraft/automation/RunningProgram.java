package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;
import net.minecraft.world.World;

public class RunningProgram {
    private MinecraftComputerExecutionContext _executionContext;
    private ComputerData _computerData;
    private int _speedConsumed;
    private boolean _running = true;

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
}
