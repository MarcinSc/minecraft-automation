package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerSpec;
import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAutomationRegistry implements AutomationRegistry {
	private Map<Integer, Map<Integer, ComputerModule>> _modulesByItemId = new HashMap<Integer, Map<Integer, ComputerModule>>();
	private Map<String, Item> _moduleItemsByType = new HashMap<String, Item>();
	private Map<String, Integer> _moduleMetadataByType = new HashMap<String, Integer>();
	private Map<Integer, ComputerSpec> _computerSpecsByBlockId = new HashMap<Integer, ComputerSpec>();
	private Map<String, ComputerSpec> _computerSpecByType = new HashMap<String, ComputerSpec>();

	@Override
	public ComputerModule getModuleByItemId(int itemId, int metadata) {
		final Map<Integer, ComputerModule> moduleMetadataMap = _modulesByItemId.get(itemId);
		if (moduleMetadataMap == null)
			return null;
		return moduleMetadataMap.get(metadata);
	}

	@Override
	public Item getModuleItemByType(String moduleType) {
		return _moduleItemsByType.get(moduleType);
	}

	@Override
	public int getModuleItemMetadataByType(String moduleType) {
		return _moduleMetadataByType.get(moduleType);
	}

	@Override
	public Collection<Integer> getModuleItemMetadataForItem(int itemId) {
		final Map<Integer, ComputerModule> moduleMetadata = _modulesByItemId.get(itemId);
		if (moduleMetadata == null)
			return Collections.emptySet();
		return moduleMetadata.keySet();
	}

	@Override
	public void registerComputerModule(Item moduleItem, int metadata, ComputerModule module) {
		Map<Integer, ComputerModule> moduleMetadataMap = _modulesByItemId.get(moduleItem.itemID);
		if (moduleMetadataMap == null) {
			moduleMetadataMap = new HashMap<Integer, ComputerModule>();
			_modulesByItemId.put(moduleItem.itemID, moduleMetadataMap);
		}
		moduleMetadataMap.put(metadata, module);
		_moduleItemsByType.put(module.getModuleType(), moduleItem);
		_moduleMetadataByType.put(module.getModuleType(), metadata);
	}

	@Override
	public void registerComputerSpec(Block computerBlock, ComputerSpec computerSpec) {
		_computerSpecsByBlockId.put(computerBlock.blockID, computerSpec);
		_computerSpecByType.put(computerSpec.computerType, computerSpec);
	}

	@Override
	public ComputerSpec getComputerSpecByType(String computerType) {
		return _computerSpecByType.get(computerType);
	}
}
