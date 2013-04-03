package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.statement.ConstantStatement;
import com.gempukku.minecraft.automation.lang.statement.ReturnStatement;

import java.util.Map;

public class MapPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(Variable object, String property) throws ExecutionException {
        Map<String, Variable> map = (Map<String, Variable>) object.getValue();
        if (property.equals("size")) {
            FunctionExecutable function = new FunctionExecutable(new String[0]);
            function.setStatement(
                    new ReturnStatement(new ConstantStatement(new Variable(map.size()))));
            return new Variable(function);
        }
        return null;
    }
}
