package com.gempukku.minecraft.automation.lang;

public enum Operator {
    MEMBER_ACCESS(1, 1, true), FUNCTION_CALL(0, 1, true),
    MULTIPLY(1, 3, true), DIVIDE(1, 3, true), MOD(1, 3, true),
    ADD(1, 4, true), SUBTRACT(1, 4, true),
    EQUALS(2, 7, true), NOT_EQUALS(2, 7, true),
    ASSIGNMENT(1, 14, true);

    private int _priority;
    private int _consumeLength;
    private boolean _leftAssociative;

    private Operator(int consumeLength, int priority, boolean leftAssociative) {
        _consumeLength = consumeLength;
        _priority = priority;
        _leftAssociative = leftAssociative;
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
}
