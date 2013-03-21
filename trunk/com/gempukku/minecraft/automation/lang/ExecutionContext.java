package com.gempukku.minecraft.automation.lang;

import java.util.LinkedList;
import java.util.List;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> _executionsInBlocks = new LinkedList<LinkedList<Execution>>();
    private Variable _contextValue;
    private boolean _returnFromFunction;
    private boolean _breakFromBlock;
    private LinkedList<CallContext> _blocksCallContext = new LinkedList<CallContext>();

    public void stackExecution(Execution execution) {
        _executionsInBlocks.getLast().add(execution);
    }

    public ExecutionProgress executeNext() throws IllegalSyntaxException {
        while (!_executionsInBlocks.isEmpty()) {
            final LinkedList<Execution> inBlockExecutionStack = _executionsInBlocks.getLast();
            while (!inBlockExecutionStack.isEmpty()) {
                final Execution execution = inBlockExecutionStack.getLast();
                if (execution.hasNextExecution(this)) {
                    final ExecutionProgress executionProgress = execution.executeNextStatement(this);
                    if (_breakFromBlock)
                        exitBlock();
                    if (_returnFromFunction)
                        exitFunction(true);
                    return executionProgress;
                } else
                    inBlockExecutionStack.removeLast();
            }
            exitFunction(false);
        }
        return null;
    }

    public void breakFromBlock() {
        _breakFromBlock = true;
    }

    public void returnFromFunction() {
        _returnFromFunction = true;
    }

    private void exitBlock() {
        _executionsInBlocks.removeLast();
        _blocksCallContext.removeLast();
        _contextValue = null;
        _breakFromBlock = false;
    }

    private void exitFunction(boolean returnResult) {
        boolean exited = false;
        while (!exited) {
            _executionsInBlocks.removeLast();
            exited = _blocksCallContext.removeLast().isFunctionContext();
        }
        if (!returnResult)
            _contextValue = null;
        _returnFromFunction = false;
    }

    public Variable getContextValue() {
        return _contextValue;
    }

    public void setContextValue(Variable contextValue) {
        _contextValue = contextValue;
    }

    public CallContext peekCallContext() {
        return _blocksCallContext.getLast();
    }

    public void stackBlockCall(CallContext callContext, Execution execution) {
        _blocksCallContext.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        _executionsInBlocks.add(functionExecutionStack);
    }

    public boolean isFinished() {
        return _executionsInBlocks.isEmpty();
    }

    public ExecutionProgress executeMethod(Variable object, String methodName, List<Variable> parameterValues) {
        // TODO
        return new ExecutionProgress(100);
    }
}
