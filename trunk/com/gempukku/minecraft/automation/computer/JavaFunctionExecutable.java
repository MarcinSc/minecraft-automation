package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public abstract class JavaFunctionExecutable implements FunctionExecutable {
    @Override
    public final Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(getDuration(), 0,
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                        final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) context;
                        ComputerCallback computer = minecraftExecutionContext.getComputerCallback();

                        final String[] parameterNames = getParameterNames();
                        Map<String, Variable> parameters = new HashMap<String, Variable>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames)
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));

                        context.setReturnValue(new Variable(executeFunction(line, minecraftExecutionContext.getWorld(), computer, parameters)));
                        return new ExecutionProgress(ExecutionTimes.SET_RETURN_VALUE);
                    }
                });
    }

    @Override
    public final CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    /**
     * Returns duration of the operation in computer cycles. Used to effectively throttle computer programs.
     *
     * @return Duration in computer cycles.
     */
    protected abstract int getDuration();

    /**
     * Executes this function, gets passed an instance of the computer this function is executed on, as well as
     * parameters passed to the function, as defined by getParameterNames method in this class. The returned object
     * will be placed into context that called this function. It is advisable to use only basic objects as return values.
     * Numbers (int, float), booleans, Strings and null.
     * If an execution of the program should be stopped due to a fatal exception, ExecutionException should be thrown by
     * the method.
     *
     * @param line       Line where the call to the function was made.
     * @param world      World this computer is in.
     * @param computer   Computer this function is executed on.
     * @param parameters Parameters that were sent to this function.
     * @return Object that has to be set in context of the caller (return value).
     * @throws ExecutionException Fatal exception that will be communicated to the computer console. When thrown,
     *                            the execution of the program will stop.
     */
    protected abstract Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException;
}
