package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
    private List<ExecutableStatement> _statements;

    public BlockStatement(List<ExecutableStatement> statements) {
        _statements = statements;
    }

    public Execution createExecution() {
        return new Execution() {
            private boolean _stacked;
            @Override
            public boolean hasNextExecution(ExecutionContext executionContext) {
                return !_stacked;
            }

            @Override
            public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
                final CallContext parentContext = executionContext.peekCallContext();

                CallContext blockContext = new CallContext(parentContext);
                blockContext.setFunctionContext(false);
                executionContext.stackBlockCall(blockContext, new MultiStatementExecution(_statements));
                _stacked = true;
                return new ExecutionProgress(100);
            }
        };
    }
}
