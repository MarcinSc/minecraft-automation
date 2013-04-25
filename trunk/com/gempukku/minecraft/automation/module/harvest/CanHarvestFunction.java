package com.gempukku.minecraft.automation.module.harvest;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Map;

public class CanHarvestFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"direction"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable directionVar = parameters.get("direction");
		if (directionVar.getType() != Variable.Type.STRING && directionVar.getType() != Variable.Type.NULL)
			throw new ExecutionException(line, "Invalid direction received in harvest()");

		String side = (String) directionVar.getValue();
		if (side != null && (!side.equals("up") || !side.equals("down")))
			throw new ExecutionException(line, "Invalid direction received in harvest()");

		final int facing = computer.getFacing();

		int direction = facing;
		if (side != null) {
			if (side.equals("up"))
				direction = BoxSide.TOP;
			else if (side.equals("down"))
				direction = BoxSide.BOTTOM;
		}

		final int harvestX = computer.getX() + Facing.offsetsXForSide[direction];
		final int harvestY = computer.getY() + Facing.offsetsYForSide[direction];
		final int harvestZ = computer.getZ() + Facing.offsetsZForSide[direction];

		if (!world.getChunkProvider().chunkExists(harvestX >> 4, harvestZ >> 4))
			return false;

		final Block block = Block.blocksList[world.getBlockId(harvestX, harvestY, harvestZ)];
		if (block == null)
			// Can't harvest air
			return false;

		final float blockHardness = block.getBlockHardness(world, harvestX, harvestY, harvestZ);

		return ForgeEventFactory.doPlayerHarvestCheck(new FakePlayer(world, computer.getOwner()), block, blockHardness >= 0);
	}
}
