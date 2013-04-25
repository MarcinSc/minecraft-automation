package com.gempukku.minecraft.automation.module.gps;

import com.gempukku.minecraft.automation.Automation;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputerModuleItem extends Item {
	private Map<Integer, Icon> _modulesIcon = new HashMap<Integer, Icon>();

	public ComputerModuleItem(int id) {
		super(id);
		setUnlocalizedName("computerModule");
		setCreativeTab(CreativeTabs.tabMisc);
		setHasSubtypes(true);
	}

	@Override
	public void updateIcons(IconRegister iconRegister) {
		_modulesIcon.put(Automation.POSITIONING_MODULE_METADATA, iconRegister.registerIcon("automation:gps"));
		_modulesIcon.put(Automation.STORAGE_MODULE_METADATA, iconRegister.registerIcon("automation:storage"));
		_modulesIcon.put(Automation.MOBILITY_MODULE_METADATA, iconRegister.registerIcon("automation:mobility"));
		_modulesIcon.put(Automation.HARVEST_MODULE_METADATA, iconRegister.registerIcon("automation:harvest"));
	}

	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		return getIconFromDamage(stack.getItemDamage());
	}

	@Override
	public Icon getIconFromDamage(int damage) {
		return _modulesIcon.get(damage);
	}

	@Override
	public String getItemDisplayName(ItemStack itemStack) {
		return Automation.proxy.getRegistry().getModuleByItemId(itemStack.itemID, itemStack.getItemDamage()).getModuleName();
	}

	@Override
	public void getSubItems(int itemId, CreativeTabs creativeTab, List listOfStacks) {
		for (int metadata : Automation.proxy.getRegistry().getModuleItemMetadataForItem(itemId))
			listOfStacks.add(new ItemStack(itemId, 1, metadata));
	}
}
