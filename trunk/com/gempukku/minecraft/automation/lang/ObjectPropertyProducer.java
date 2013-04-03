package com.gempukku.minecraft.automation.lang;

public class ObjectPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(Variable object, String property) throws ExecutionException {
        final ObjectDefinition objectDefinition = (ObjectDefinition) object.getValue();
        return objectDefinition.getMember(property);
    }
}
