package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.lang.ExecutionException;

public interface ProgramScheduler {
    public void addRunningProgram(RunningProgram runningProgram);

    public void processRunningProgram(RunningProgram runningProgram) throws ExecutionException;

    public void removeRunningProgram(RunningProgram runningProgram);
}
