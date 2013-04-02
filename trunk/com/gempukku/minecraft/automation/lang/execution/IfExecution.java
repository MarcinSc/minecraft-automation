package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.IfStatement;

import java.util.List;

public class IfExecution implements Execution {
    private List<IfStatement.ConditionStatement> _conditionStatements;
    private ExecutableStatement _elseStatement;

    private boolean _foundCondition;
    private int _nextConditionStackedIndex = 0;
    private int _nextStatementStackedIfNeededIndex = 0;
    private boolean _elseStacked;

    public IfExecution(List<IfStatement.ConditionStatement> conditionStatements, ExecutableStatement elseStatement) {
        _conditionStatements = conditionStatements;
        _elseStatement = elseStatement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_foundCondition)
            return false;

        if (_nextStatementStackedIfNeededIndex < _nextConditionStackedIndex)
            return true;
        if (_nextConditionStackedIndex < _conditionStatements.size())
            return true;
        if (!_elseStacked && _elseStatement != null)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (_nextStatementStackedIfNeededIndex < _nextConditionStackedIndex) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException("Condition not of type BOOLEAN");
            boolean ifResult = (Boolean) value.getValue();
            if (ifResult) {
                _foundCondition = true;
                executionContext.stackExecution(_conditionStatements.get(_nextStatementStackedIfNeededIndex).getStatement().createExecution());
            }
            _nextStatementStackedIfNeededIndex++;
            return new ExecutionProgress(100);
        }
        if (_nextConditionStackedIndex < _conditionStatements.size()) {
            executionContext.stackExecution(_conditionStatements.get(_nextConditionStackedIndex).getCondition().createExecution());
            _nextConditionStackedIndex++;
            return new ExecutionProgress(100);
        }
        if (!_elseStacked) {
            executionContext.stackExecution(_elseStatement.createExecution());
            _elseStacked = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
