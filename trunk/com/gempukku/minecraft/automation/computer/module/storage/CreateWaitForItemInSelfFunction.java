package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.*;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class CreateWaitForItemInSelfFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"slot"};
	}

	@Override
	protected Object executeFunction(final int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable slotVar = parameters.get("slot");
		if (slotVar.getType() != Variable.Type.NUMBER && slotVar.getType() != Variable.Type.NULL)
			throw new ExecutionException(line, "Expected NUMBER or NULL in createWaitForItemInSelf()");

		final ComputerTileEntity computerEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerEntity == null)
			return null;

		final int slot = slotVar.getType() == Variable.Type.NULL ? -1 : ((Number) slotVar.getValue()).intValue();
		if (slot < -2 || slot >= computerEntity.getItemSlotsCount())
			throw new ExecutionException(line, "Slot number out of accepted range in createWaitForItemInSelf()");

		return new AbstractConditionCustomObject() {
			@Override
			public int getCreationDelay() {
				return 0;
			}

			@Override
			public ResultAwaitingCondition createAwaitingCondition() {
				return new PeriodicCheckResultAwaitingCondition(new CheckSelfSlotAwaitingCondition(line, slot), 10);
			}
		};
	}

	private static class CheckSelfSlotAwaitingCondition implements ResultAwaitingCondition {
		private int _foundInSlot;
		private int _line;
		private int _slot;

		private CheckSelfSlotAwaitingCondition(int line, int slot) {
			_line = line;
			_slot = slot;
		}

		@Override
		public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException {
			final ComputerTileEntity computerEntity = AutomationUtils.getComputerEntitySafely(world, computer);
			if (computerEntity == null)
				return false;

			final int itemSlotsCount = computerEntity.getItemSlotsCount();
			if (_slot >= itemSlotsCount)
				throw new ExecutionException(_line, "Slot number out of accepted range while waiting for createWaitForItemInSelf()");

			if (_slot == -1) {
				for (int i = 0; i < itemSlotsCount; i++)
					if (computerEntity.getStackInSlot(_slot) != null) {
						_foundInSlot = i;
						return true;
					}
			} else if (computerEntity.getStackInSlot(_slot) != null) {
				_foundInSlot = _slot;
				return true;
			}
			return false;
		}

		@Override
		public Variable getReturnValue() {
			return new Variable(_foundInSlot);
		}
	}
}
