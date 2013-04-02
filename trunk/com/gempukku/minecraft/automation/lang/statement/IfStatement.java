package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.IfExecution;

import java.util.ArrayList;
import java.util.List;

public class IfStatement implements ExecutableStatement {
    private List<ConditionStatement> _conditionStatements = new ArrayList<ConditionStatement>();
    private ExecutableStatement _elseStatement;

    public IfStatement(ExecutableStatement condition, ExecutableStatement statement) {
        _conditionStatements.add(new ConditionStatement(condition, statement));
    }

    @Override
    public Execution createExecution() {
        return new IfExecution(_conditionStatements, _elseStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    public void addElseIf(ExecutableStatement condition, ExecutableStatement statement) {
        _conditionStatements.add(new ConditionStatement(condition, statement));
    }

    public void addElse(ExecutableStatement statement) {
        _elseStatement = statement;
    }

    public static class ConditionStatement {
        private ExecutableStatement _condition;
        private ExecutableStatement _statement;

        private ConditionStatement(ExecutableStatement condition, ExecutableStatement statement) {
            _condition = condition;
            _statement = statement;
        }

        public ExecutableStatement getCondition() {
            return _condition;
        }

        public ExecutableStatement getStatement() {
            return _statement;
        }
    }
}
