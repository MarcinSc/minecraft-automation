package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.List;

public class WhileExecution implements Execution {
    private ExecutableStatement _condition;
    private List<ExecutableStatement> _statements;

    private boolean _stackedCondition = false;
    private boolean _stackedStatementsIfPassed = false;

    public WhileExecution(ExecutableStatement condition, List<ExecutableStatement> statements) {
        _condition = condition;
        _statements = statements;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedCondition)
            return true;
        if (!_stackedStatementsIfPassed)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (!_stackedCondition) {
            executionContext.stackExecution(_condition.createExecution());
            _stackedCondition = true;
            _stackedStatementsIfPassed = false;
            return new ExecutionProgress(100);
        }
        if (!_stackedStatementsIfPassed) {
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN)
                throw new IllegalSyntaxException("While condition not of boolean type");
            final Boolean result = (Boolean) contextValue.getValue();
            if (result) {
                final CallContext parentContext = executionContext.peekCallContext();

                CallContext blockContext = new CallContext(parentContext);
                blockContext.setFunctionContext(false);
                executionContext.stackBlockCall(blockContext, new MultiStatementExecution(_statements));
                _stackedCondition = false;
            }
            _stackedStatementsIfPassed = true;
            return new ExecutionProgress(100);
        }

        return null;
    }
}
