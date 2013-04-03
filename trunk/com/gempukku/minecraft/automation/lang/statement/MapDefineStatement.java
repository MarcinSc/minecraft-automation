package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.execution.MapDefineExecution;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapDefineStatement implements ExecutableStatement {
    private Map<String, ExecutableStatement> _properties = new LinkedHashMap<String, ExecutableStatement>();

    public MapDefineStatement() {

    }

    public void addProperty(String name, ExecutableStatement statement) throws IllegalSyntaxException {
        if (_properties.containsKey(name))
            throw new IllegalSyntaxException("This map already contains an entry for this name");

        _properties.put(name, statement);
    }

    @Override
    public Execution createExecution() {
        return new MapDefineExecution(_properties);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
