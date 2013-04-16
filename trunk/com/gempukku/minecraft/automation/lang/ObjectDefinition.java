package com.gempukku.minecraft.automation.lang;

public interface ObjectDefinition {
    public Variable getMember(ExecutionContext context, String name);
}
