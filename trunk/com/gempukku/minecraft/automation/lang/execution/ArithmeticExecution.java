package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.ArithmeticStatement;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticExecution implements Execution {
    private List<ExecutableStatement> _statements;
    private List<ArithmeticStatement.Oper> _operators;

    private List<Variable> _statementResults = new ArrayList<Variable>();

    private int _nextStatementToStack = 0;
    private boolean _calculatedResult = false;

    public ArithmeticExecution(List<ExecutableStatement> statements, List<ArithmeticStatement.Oper> operators) {
        _statements = statements;
        _operators = operators;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_nextStatementToStack < _statements.size())
            return true;
        if (!_calculatedResult)
            return true;

        return false;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (_nextStatementToStack < _statements.size()) {
            executionContext.stackExecution(_statements.get(_nextStatementToStack).createExecution());
            _nextStatementToStack++;
            return new ExecutionProgress(100);
        }
        // TODO For now, just go left to right
        Variable result = _statementResults.get(0);

        for (int i = 1; i < _statementResults.size(); i++) {
            final Variable nextValue = _statementResults.get(i);
            final ArithmeticStatement.Oper operator = _operators.get(i - 1);
            if (operator == ArithmeticStatement.Oper.ADD) {
                if (nextValue.getType() == Variable.Type.NUMBER
                        && result.getType() == Variable.Type.NUMBER) {
                    result = new Variable(((Number) result.getValue()).floatValue() + ((Number) nextValue.getValue()).floatValue());
                } else if (nextValue.getType() == Variable.Type.STRING
                        || result.getType() == Variable.Type.STRING) {
                    result = new Variable("" + result.getValue() + nextValue.getValue());
                } else {
                    throw new IllegalSyntaxException("Unable to add values of types "+result.getType()+" and "+nextValue.getType());
                }
            } else if (operator == ArithmeticStatement.Oper.SUBTRACT) {
                if (nextValue.getType() == Variable.Type.NUMBER
                        && result.getType() == Variable.Type.NUMBER) {
                    result = new Variable(((Number) result.getValue()).floatValue() - ((Number) nextValue.getValue()).floatValue());
                } else {
                    throw new IllegalSyntaxException("Unable to subtract values of types "+result.getType()+" and "+nextValue.getType());
                }
            } else if (operator == ArithmeticStatement.Oper.MULTIPLY) {
                if (nextValue.getType() == Variable.Type.NUMBER
                        && result.getType() == Variable.Type.NUMBER) {
                    result = new Variable(((Number) result.getValue()).floatValue() * ((Number) nextValue.getValue()).floatValue());
                } else {
                    throw new IllegalSyntaxException("Unable to multiply values of types "+result.getType()+" and "+nextValue.getType());
                }
            } else if (operator == ArithmeticStatement.Oper.DIVIDE) {
                if (nextValue.getType() == Variable.Type.NUMBER
                        && result.getType() == Variable.Type.NUMBER) {
                    result = new Variable(((Number) result.getValue()).floatValue() / ((Number) nextValue.getValue()).floatValue());
                } else {
                    throw new IllegalSyntaxException("Unable to divide values of types "+result.getType()+" and "+nextValue.getType());
                }
            } else if (operator == ArithmeticStatement.Oper.MOD) {
                if (nextValue.getType() == Variable.Type.NUMBER
                        && result.getType() == Variable.Type.NUMBER) {
                    result = new Variable(((Number) result.getValue()).intValue() % ((Number) nextValue.getValue()).intValue());
                } else {
                    throw new IllegalSyntaxException("Unable to mod values of types "+result.getType()+" and "+nextValue.getType());
                }
            }
        }
        executionContext.setContextValue(result);

        return new ExecutionProgress(100);
    }
}
