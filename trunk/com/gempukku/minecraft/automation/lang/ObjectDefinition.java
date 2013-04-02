package com.gempukku.minecraft.automation.lang;

import java.util.HashMap;
import java.util.Map;

public class ObjectDefinition {
    public Map<String, Variable> _members = new HashMap<String, Variable>();

    public void addMember(String name, Object value) {
        _members.put(name, new Variable(value));
    }

    public Variable getMember(String name) {
        return _members.get(name);
    }
}
