package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.AbstractConditionCustomObject;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ResultAwaitingCondition;
import com.gempukku.minecraft.automation.lang.CustomObject;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"conditions"};
	}

	@Override
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable conditionsVar = parameters.get("conditions");
		if (conditionsVar.getType() != Variable.Type.LIST)
			throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");

		List<Variable> conditions = (List<Variable>) conditionsVar.getValue();

		int delay = 0;
		final List<AbstractConditionCustomObject> allConditions = new ArrayList<AbstractConditionCustomObject>();
		for (Variable condition : conditions) {
			if (condition.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) condition.getValue()).getType().equals("CONDITION"))
				throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");
			final AbstractConditionCustomObject conditionDefinition = (AbstractConditionCustomObject) condition.getValue();
			delay = Math.max(delay, conditionDefinition.getCreationDelay());
			allConditions.add(conditionDefinition);
		}

		final int maxDelay = delay;

		return new AbstractConditionCustomObject() {
			@Override
			public int getCreationDelay() {
				return maxDelay;
			}

			@Override
			public ResultAwaitingCondition createAwaitingCondition() {
				List<ResultAwaitingCondition> allConditionList = new ArrayList<ResultAwaitingCondition>();
				for (AbstractConditionCustomObject allCondition : allConditions)
					allConditionList.add(allCondition.createAwaitingCondition());

				return new AllResultAwaitingCondition(allConditionList);
			}
		};
	}
}
