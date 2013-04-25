package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ResultAwaitingCondition;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.*;

public class AllResultAwaitingCondition implements ResultAwaitingCondition {
	private List<ResultAwaitingCondition> _awaitingConditions;
	private Set<ResultAwaitingCondition> _notMetConditions;

	public AllResultAwaitingCondition(List<ResultAwaitingCondition> awaitingConditions) {
		_awaitingConditions = awaitingConditions;
		_notMetConditions = new LinkedHashSet<ResultAwaitingCondition>(_awaitingConditions);
	}

	@Override
	public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException {
		final Iterator<ResultAwaitingCondition> notMetIterator = _notMetConditions.iterator();
		while (notMetIterator.hasNext()) {
			final ResultAwaitingCondition notMetCondition = notMetIterator.next();
			if (notMetCondition.isMet(checkAttempt, world, computer))
				notMetIterator.remove();

		}
		return _notMetConditions.isEmpty();
	}

	@Override
	public Variable getReturnValue() {
		List<Variable> result = new ArrayList<Variable>();
		for (ResultAwaitingCondition awaitingCondition : _awaitingConditions)
			result.add(awaitingCondition.getReturnValue());

		return new Variable(result);
	}
}
