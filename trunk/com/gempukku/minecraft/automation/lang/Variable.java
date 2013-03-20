package com.gempukku.minecraft.automation.lang;

import java.util.Map;

public class Variable {
    public enum Type { NULL, STRING, NUMBER, ARRAY, MAP }

    private Object _value;
    private Type _type;

    public Variable(Object value) {
        setValue(value);
    }

    public void setValue(Object value) {
        if (value == null) {
            _type = Type.NULL;
            _value = null;
        } else if (value instanceof String) {
            _type = Type.STRING;
            _value = value;
        } else if (value instanceof Number) {
            _type = Type.NUMBER;
            _value = value;
        } else if (value instanceof Map) {
            _type = Type.MAP;
            _value = value;
        } else if (value.getClass().isArray()) {
            _type = Type.ARRAY;
            _value = value;
        } else
            throw new UnsupportedOperationException("Unkown type of variable value: "+value.getClass().getSimpleName());
    }

    public Type getType() {
        return _type;
    }

    public Object getValue() throws IllegalSyntaxException {
        return _value;
    }
}
