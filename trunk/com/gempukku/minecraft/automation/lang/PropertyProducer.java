package com.gempukku.minecraft.automation.lang;

public interface PropertyProducer {
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException;
}
