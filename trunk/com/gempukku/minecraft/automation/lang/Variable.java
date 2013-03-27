package com.gempukku.minecraft.automation.lang;

import java.util.Map;

public class Variable {
    public enum Type { NULL, STRING, NUMBER, BOOLEAN, ARRAY, MAP }

    private Object _value;
    private Type _type;

    public Variable(Object value) {
        setValue(value);
    }

    public void setValue(Object value) {
        _value = value;
        if (value == null) {
            _type = Type.NULL;
        } else if (value instanceof String) {
            _type = Type.STRING;
        } else if (value instanceof Number) {
            _type = Type.NUMBER;
        } else if (value instanceof Map) {
            _type = Type.MAP;
        } else if (value.getClass().isArray()) {
            _type = Type.ARRAY;
        } else if (value instanceof Boolean) {
            _type = Type.BOOLEAN;
        } else
            throw new UnsupportedOperationException("Unkown type of variable value: "+value.getClass().getSimpleName());
    }

    public Type getType() {
        return _type;
    }

    public Object getValue() {
        return _value;
    }
}
