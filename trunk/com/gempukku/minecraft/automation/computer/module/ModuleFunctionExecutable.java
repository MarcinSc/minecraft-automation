package com.gempukku.minecraft.automation.computer.module;

import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public interface ModuleFunctionExecutable {
    public int getDuration();

    public int getMinimumExecutionTicks();

    public String[] getParameterNames();

    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException;
}
