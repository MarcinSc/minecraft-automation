package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringPropertyProducer implements PropertyProducer {
	@Override
	public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
		String text = (String) object.getValue();
		if (property.equals("split"))
			return new Variable(new SplitFunctionExecutable(text));
		return new Variable(null);
	}

	private static class SplitFunctionExecutable extends JavaFunctionExecutable {
		private String _text;

		private SplitFunctionExecutable(String text) {
			_text = text;
		}

		@Override
		protected int getDuration() {
			return 100;
		}

		@Override
		public String[] getParameterNames() {
			return new String[]{"separator", "limit"};
		}

		@Override
		protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
			final Variable limitVar = parameters.get("limit");
			int limit;
			if (limitVar.getType() == Variable.Type.NULL) {
				limit = 0;
			} else if (limitVar.getType() == Variable.Type.NUMBER) {
				limit = ((Number) limitVar.getValue()).intValue();
			} else {
				throw new ExecutionException("Expected NUMBER or NULL");
			}
			if (limit < 0)
				limit = 0;

			final Variable separatorVar = parameters.get("separator");
			if (separatorVar.getType() != Variable.Type.STRING)
				throw new ExecutionException("Expected STRING");

			String separator = (String) separatorVar.getValue();
			final String[] splitResult = _text.split(separator, limit);
			List<Variable> result = new ArrayList<Variable>();
			for (String split : splitResult)
				result.add(new Variable(split));

			return result;
		}
	}
}
