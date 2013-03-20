package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.ArithmeticStatement;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticExecution implements Execution {
    private List<ExecutableStatement> _statements;
    private List<ArithmeticStatement.Oper> _operators;

    private List<Variable> _statementResults = new ArrayList<Variable>();

    private int _nextStatementToStackIndex = 0;
    private int _nextStatementValueToRetrieveIndex = 0;
    private boolean _calculatedResult = false;

    public ArithmeticExecution(List<ExecutableStatement> statements, List<ArithmeticStatement.Oper> operators) {
        _statements = statements;
        _operators = operators;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_nextStatementValueToRetrieveIndex <_statements.size())
            return true;
        if (_nextStatementToStackIndex < _statements.size())
            return true;
        if (!_calculatedResult)
            return true;

        return false;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (_nextStatementValueToRetrieveIndex < _nextStatementToStackIndex) {
            _statementResults.add(executionContext.getContextValue());
            _nextStatementValueToRetrieveIndex++;
            return new ExecutionProgress(100);
        }
        if (_nextStatementToStackIndex < _statements.size()) {
            executionContext.stackExecution(_statements.get(_nextStatementToStackIndex).createExecution());
            _nextStatementToStackIndex++;
            return new ExecutionProgress(100);
        }
        if (!_calculatedResult) {
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
                        throw new IllegalSyntaxException("Unable to add values of types " + result.getType() + " and " + nextValue.getType());
                    }
                } else if (operator == ArithmeticStatement.Oper.SUBTRACT) {
                    if (nextValue.getType() == Variable.Type.NUMBER
                            && result.getType() == Variable.Type.NUMBER) {
                        result = new Variable(((Number) result.getValue()).floatValue() - ((Number) nextValue.getValue()).floatValue());
                    } else {
                        throw new IllegalSyntaxException("Unable to subtract values of types " + result.getType() + " and " + nextValue.getType());
                    }
                } else if (operator == ArithmeticStatement.Oper.MULTIPLY) {
                    if (nextValue.getType() == Variable.Type.NUMBER
                            && result.getType() == Variable.Type.NUMBER) {
                        result = new Variable(((Number) result.getValue()).floatValue() * ((Number) nextValue.getValue()).floatValue());
                    } else {
                        throw new IllegalSyntaxException("Unable to multiply values of types " + result.getType() + " and " + nextValue.getType());
                    }
                } else if (operator == ArithmeticStatement.Oper.DIVIDE) {
                    if (nextValue.getType() == Variable.Type.NUMBER
                            && result.getType() == Variable.Type.NUMBER) {
                        result = new Variable(((Number) result.getValue()).floatValue() / ((Number) nextValue.getValue()).floatValue());
                    } else {
                        throw new IllegalSyntaxException("Unable to divide values of types " + result.getType() + " and " + nextValue.getType());
                    }
                } else if (operator == ArithmeticStatement.Oper.MOD) {
                    if (nextValue.getType() == Variable.Type.NUMBER
                            && result.getType() == Variable.Type.NUMBER) {
                        result = new Variable(((Number) result.getValue()).floatValue() % ((Number) nextValue.getValue()).floatValue());
                    } else {
                        throw new IllegalSyntaxException("Unable to mod values of types " + result.getType() + " and " + nextValue.getType());
                    }
                }
            }
            executionContext.setContextValue(result);
            _calculatedResult = true;
            return new ExecutionProgress(100);
        }
        throw new IllegalStateException("Should not get here");
    }
}
