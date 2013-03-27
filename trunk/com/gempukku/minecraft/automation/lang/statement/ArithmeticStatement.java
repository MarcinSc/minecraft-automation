package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;

import java.util.List;

public class ArithmeticStatement implements ExecutableStatement {
    public enum Oper {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUALS, NOT_EQUALS
    }

    private List<ExecutableStatement> _statements;
    private List<Oper> _operations;

    public ArithmeticStatement(List<ExecutableStatement> statements, List<Oper> operations) {
        _statements = statements;
        _operations = operations;
    }

    @Override
    public Execution createExecution() {
        return null;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
