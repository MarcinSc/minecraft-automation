package com.gempukku.minecraft.automation.lang;

public enum Operator {
    MEMBER_ACCESS(1, 1, true, true, false), MAPPED_ACCESS(1, 1, true, false, true, "]"), FUNCTION_CALL(1, 1, true, false, true, ")"),
    NOT(1, 2, false, false, false),
    MULTIPLY(1, 3, true, true, false), DIVIDE(1, 3, true, true, false), MOD(1, 3, true, true, false),
    ADD(1, 4, true, true, false), SUBTRACT(1, 4, true, true, false),
    GREATER_OR_EQUAL(2, 6, true, true, false), GREATER(1, 6, true, true, false), LESS_OR_EQUAL(2, 6, true, true, false), LESS(1, 6, true, true, false),
    EQUALS(2, 7, true, true, false), NOT_EQUALS(2, 7, true, true, false),
    AND(2, 11, true, true, false),
    OR(2, 12, true, true, false),
    ASSIGNMENT(1, 14, true, true, false);

    private int _priority;
    private int _consumeLength;
    private boolean _leftAssociative;
    private boolean _binary;
    private boolean _hasParameters;
    private String _parametersClosing;

    private Operator(int consumeLength, int priority, boolean leftAssociative, boolean binary, boolean hasParameters) {
        this(consumeLength, priority, leftAssociative, binary, hasParameters, null);
    }
    
    private Operator(int consumeLength, int priority, boolean leftAssociative, boolean binary, boolean hasParameters, String parametersClosing) {
        _consumeLength = consumeLength;
        _priority = priority;
        _leftAssociative = leftAssociative;
        _binary = binary;
        _hasParameters = hasParameters;
        _parametersClosing =parametersClosing;
    }

    public int getConsumeLength() {
        return _consumeLength;
    }

    public boolean isLeftAssociative() {
        return _leftAssociative;
    }

    public int getPriority() {
        return _priority;
    }

    public boolean isBinary() {
        return _binary;
    }

    public boolean isHasParameters() {
        return _hasParameters;
    }

    public String getParametersClosing() {
        return _parametersClosing;
    }
}
