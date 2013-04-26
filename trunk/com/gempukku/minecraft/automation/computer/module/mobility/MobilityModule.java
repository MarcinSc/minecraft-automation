package com.gempukku.minecraft.automation.computer.module.mobility;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.computer.module.ComputerModuleUtils;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import net.minecraft.world.World;

public class MobilityModule extends AbstractComputerModule {
	public static final String TYPE = "Mobility";
	private MoveFunction _move = new MoveFunction();
	private CanMoveFunction _canMove = new CanMoveFunction();
	private TurnFunction _turnLeftFunction = new TurnFunction(true);
	private TurnFunction _turnRightFunction = new TurnFunction(false);

	@Override
	public boolean acceptsNewModule(World world, ComputerCallback computerCallback, ComputerModule computerModule) {
		return !computerModule.getModuleType().equals(TYPE);
	}

	@Override
	public boolean canBePlacedInComputer(World world, ComputerCallback computerCallback) {
		return !ComputerModuleUtils.hasModuleOfType(world, computerCallback, TYPE);
	}

	@Override
	public String getModuleType() {
		return TYPE;
	}

	@Override
	public String getModuleName() {
		return "Mobility module";
	}

	@Override
	public ModuleFunctionExecutable getFunctionByName(String name) {
		if (name.equals("move"))
			return _move;
		else if (name.equals("turnLeft"))
			return _turnLeftFunction;
		else if (name.equals("turnRight"))
			return _turnRightFunction;
		else if (name.equals("canMove"))
			return _canMove;
		return null;
	}
}
