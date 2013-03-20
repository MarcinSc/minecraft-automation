package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.ArithmeticExecution;

import java.util.List;

public class ArithmeticStatement implements ExecutableStatement {
    public enum Oper { ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD }
    
    private List<ExecutableStatement> _executableStatements;
    private List<Oper> _operations;

    public ArithmeticStatement(List<ExecutableStatement> executableStatements, List<Oper> operations) {
        _executableStatements = executableStatements;
        _operations = operations;
    }

    public Execution createExecution() {
        return new ArithmeticExecution(_executableStatements, _operations);
    }
}
