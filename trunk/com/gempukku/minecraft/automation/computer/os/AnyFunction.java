package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.AbstractConditionCustomObject;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.CustomObject;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnyFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"conditions"};
	}

	@Override
	protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable conditionsVar = parameters.get("conditions");
		if (conditionsVar.getType() != Variable.Type.LIST)
			throw new ExecutionException("Expected LIST");

		List<Variable> conditions = (List<Variable>) conditionsVar.getValue();

		int delay = 0;
		final List<AbstractConditionCustomObject> anyConditions = new ArrayList<AbstractConditionCustomObject>();
		for (Variable condition : conditions) {
			if (condition.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) condition.getValue()).getType().equals("CONDITION"))
				throw new ExecutionException("Expected CONDITION");
			final AbstractConditionCustomObject conditionDefinition = (AbstractConditionCustomObject) condition.getValue();
			delay = Math.max(delay, conditionDefinition.getCreationDelay());
			anyConditions.add(conditionDefinition);
		}

		final int maxDelay = delay;

		return new AbstractConditionCustomObject() {
			@Override
			public int getCreationDelay() {
				return maxDelay;
			}

			@Override
			public ResultAwaitingCondition createAwaitingCondition() {
				List<ResultAwaitingCondition> anyConditionList = new ArrayList<ResultAwaitingCondition>();
				for (AbstractConditionCustomObject anyCondition : anyConditions)
					anyConditionList.add(anyCondition.createAwaitingCondition());

				return new AnyResultAwaitingCondition(anyConditionList);
			}
		};
	}
}
