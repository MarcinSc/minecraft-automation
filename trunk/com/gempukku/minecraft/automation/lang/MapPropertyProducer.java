package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;

import java.util.Map;

public class MapPropertyProducer implements PropertyProducer {
	@Override
	public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
		Map<String, Variable> map = (Map<String, Variable>) object.getValue();
		if (property.equals("size"))
			return new Variable(new MapSizeFunction(map));
		return new Variable(null);
	}

	private static class MapSizeFunction extends JavaFunctionExecutable {
		private Map<String, Variable> _map;

		private MapSizeFunction(Map<String, Variable> map) {
			_map = map;
		}

		@Override
		protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
			return _map.size();
		}

		@Override
		protected int getDuration() {
			return 100;
		}

		@Override
		public String[] getParameterNames() {
			return new String[0];
		}
	}
}
