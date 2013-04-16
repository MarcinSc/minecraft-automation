package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.module.ComputerModule;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class ComputerBlock extends BlockContainer {
    private Icon _frontWorkingIcon;
    private Icon _frontReadyIcon;
    private Icon _sideIcon;

    public ComputerBlock(int id) {
        super(id, Material.ground);
        setHardness(1.5F);
        setResistance(10.0F);
        setUnlocalizedName("computer");
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
        ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
        if (computerTileEntity != null) {
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this, 1, computerTileEntity.getComputerId()));
            if (MinecraftUtils.isServer(world))
                MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerRemovedFromWorldEvent(world, computerTileEntity));
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        _frontReadyIcon = iconRegister.registerIcon("computerFrontReady");
        _frontWorkingIcon = iconRegister.registerIcon("computerFrontWorking");
        _sideIcon = iconRegister.registerIcon("computerSide");
    }

    @Override
    public Icon getBlockTextureFromSideAndMetadata(int side, int metadata) {
        if (side == metadata)
            return _frontReadyIcon;
        else
            return _sideIcon;
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        // We already dropped the items in breakBlock method, therefore not dropping here anything
        return new ArrayList<ItemStack>();
    }

    private ComputerTileEntity createTileEntityFromItemStack(World world, int computerId, String playerPlacing) {
        ComputerTileEntity result = new ComputerTileEntity();
        // If it's a new computer, on the server we have to assign an id to it
        if (computerId == 0 && MinecraftUtils.isServer(world)) {
            computerId = Automation.getServerProxy().getRegistry().storeNewComputer(playerPlacing);
            result.setModuleSlotsCount(3);
        }
        // On the client we have to forget the label for this computer, as it might change after it's placed
        if (!MinecraftUtils.isClient(world))
            Automation.getClientProxy().getRegistry().clearLabelCache(computerId);
        result.setComputerId(computerId);
        return result;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComputerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float xPos, float yPos, float zPos) {
        if (player.isSneaking())
            return false;

        final ItemStack usedItem = player.getItemInUse();
        if (usedItem != null && usedItem.getItem() == Automation.terminalItem)
            player.openGui(Automation.instance, 0, world, x, y, z);
        else
            player.openGui(Automation.instance, 1, world, x, y, z);

        return true;
    }

    public void initializeBlockAfterPlaced(World world, int x, int y, int z, int computerId, String playerPlacing) {
        ComputerTileEntity computerEntity = createTileEntityFromItemStack(world, computerId, playerPlacing);
        MinecraftUtils.updateTileEntity(world, x, y, z, computerEntity);
        if (MinecraftUtils.isServer(world))
            MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerAddedToWorldEvent(world, computerEntity));
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
        if (tileEntity != null) {
            final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
            int count = computerData.getModuleSlotCount();
            int input = 0;
            for (int i = 0; i < count; i++) {
                final ComputerModule module = computerData.getModuleAt(i);
                if (module != null)
                    input = module.getStrongRedstoneSignalStrengthOnSide(computerData, input, blockAccess, side);
            }

            return input;
        }

        return super.isProvidingStrongPower(blockAccess, x, y, z, side);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
        if (tileEntity != null) {
            final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
            int count = computerData.getModuleSlotCount();
            int input = 0;
            for (int i = 0; i < count; i++) {
                final ComputerModule module = computerData.getModuleAt(i);
                if (module != null)
                    input = module.getWeakRedstoneSignalStrengthOnSide(computerData, input, blockAccess, side);
            }

            return input;
        }

        return super.isProvidingWeakPower(blockAccess, x, y, z, side);
    }

    public boolean canProvidePower() {
        return true;
    }
}
