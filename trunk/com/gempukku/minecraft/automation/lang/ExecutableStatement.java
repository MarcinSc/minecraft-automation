package com.gempukku.minecraft.automation.lang;

public interface ExecutableStatement {
    public Execution createExecution();
    public boolean requiresSemicolon();
}
