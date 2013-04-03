package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

public class RunningProgram {
    private ExecutionContext _executionContext;
    private ComputerData _computerData;
    private int _computerSpeed;
    private int _speedConsumed;
    private boolean _running = true;

    public RunningProgram(ComputerData computerData, int computerSpeed, ExecutionContext executionContext) {
        _computerData = computerData;
        _computerSpeed = computerSpeed;
        _executionContext = executionContext;
    }

    public void progressProgram() {
        _speedConsumed -= _computerSpeed;
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
