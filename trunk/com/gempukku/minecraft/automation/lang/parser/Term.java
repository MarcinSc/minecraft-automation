package com.gempukku.minecraft.automation.lang.parser;

public class Term {
    public enum Type {PROGRAM, STRING, COMMENT}

    private Type _type;
    private String _value;
    private int _line;

    public Term(Type type, String value, int line) {
        _type = type;
        _value = value;
        _line = line;
    }

    public Type getType() {
        return _type;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value) {
        _value = value;
    }

    public int getLine() {
        return _line;
    }
}
