package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

public class PeriodicCheckResultAwaitingCondition implements ResultAwaitingCondition {
	private ResultAwaitingCondition _delegate;
	private int _checkInterval;
	private boolean _met;

	public PeriodicCheckResultAwaitingCondition(ResultAwaitingCondition delegate, int checkInterval) {
		_delegate = delegate;
		_checkInterval = checkInterval;
	}

	@Override
	public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException {
		if (_met)
			return true;

		if (checkAttempt % _checkInterval == 0)
			_met = _delegate.isMet(checkAttempt / _checkInterval, world, computer);

		return _met;
	}

	@Override
	public Variable getReturnValue() {
		return _delegate.getReturnValue();
	}
}
