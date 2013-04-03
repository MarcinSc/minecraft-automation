package com.gempukku.minecraft.automation.lang;

public interface PropertyProducer {
    public Variable exposePropertyFor(Variable object, String property) throws ExecutionException;
}
