package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.Operator;
import com.gempukku.minecraft.automation.lang.execution.MathExecution;

public class MathStatement implements ExecutableStatement {
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    public MathStatement(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public Execution createExecution() {
        return new MathExecution(_left, _operator, _right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
