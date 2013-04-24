package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ListPropertyProducer implements PropertyProducer {
	@Override
	public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
		List<Variable> list = (List<Variable>) object;
		if (property.equals("getSize"))
			return new Variable(new SizeFunction(list));
		else if (property.equals("add"))
			return new Variable(new AddElementFunction(list));
		else if (property.equals("remove"))
			return new Variable(new RemoveElementFunction(list));
		return new Variable(null);
	}

	private static class RemoveElementFunction extends JavaFunctionExecutable {
		private List<Variable> _list;

		private RemoveElementFunction(List<Variable> list) {
			_list = list;
		}

		@Override
		protected int getDuration() {
			return 100;
		}

		@Override
		public String[] getParameterNames() {
			return new String[]{"index"};
		}

		@Override
		protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
			final Variable indexVar = parameters.get("index");
			if (indexVar.getType() != Variable.Type.NUMBER)
				throw new ExecutionException("Expected NUMBER");

			int index = ((Number) indexVar.getValue()).intValue();
			if (index < 0 || index >= _list.size())
				throw new ExecutionException("Index out of bounds");

			return _list.remove(index);
		}
	}

	private static class AddElementFunction extends JavaFunctionExecutable {
		private List<Variable> _list;

		private AddElementFunction(List<Variable> list) {
			_list = list;
		}

		@Override
		protected int getDuration() {
			return 100;
		}

		@Override
		public String[] getParameterNames() {
			return new String[]{"element"};
		}

		@Override
		protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
			final Object value = parameters.get("element").getValue();
			_list.add(new Variable(value));
			return null;
		}
	}

	private static class SizeFunction extends JavaFunctionExecutable {
		private List<Variable> _list;

		private SizeFunction(List<Variable> list) {
			_list = list;
		}

		@Override
		protected int getDuration() {
			return 10;
		}

		@Override
		public String[] getParameterNames() {
			return new String[0];
		}

		@Override
		protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
			return _list.size();
		}
	}
}
