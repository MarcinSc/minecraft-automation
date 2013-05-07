package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.world.ChunkPosition;

public class ServerComputerData implements ComputerCallback {
    private ComputerConsole _console = new ComputerConsole();
    private String _computerType;
    private int _id;
    private String _label;
    private String _owner;
    private ComputerTileEntity _tileEntity;

    public ServerComputerData(int id, String owner, String computerType) {
        _id = id;
        _owner = owner;
        _computerType = computerType;
    }

    public void setTileEntity(ComputerTileEntity tileEntity) {
        _tileEntity = tileEntity;
    }

    public void resetTileEntity() {
        _tileEntity = null;
    }

    public ComputerConsole getConsole() {
        return _console;
    }

    public int getId() {
        return _id;
    }

    public int getDimension() {
        return _tileEntity.worldObj.provider.dimensionId;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public String getOwner() {
        return _owner;
    }

    public String getComputerType() {
        return _computerType;
    }

    public int getSpeed() {
        return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).speed;
    }

    public int getMaxStackSize() {
        return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).maxStackSize;
    }

    public int getMaxMemory() {
        return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).memory;
    }

    public void appendToConsole(String text) {
        _console.appendString(text);
    }

    public int getFacing() {
        return _tileEntity.getFacing();
    }

    public ChunkPosition getChunkPosition() {
        return new ChunkPosition(_tileEntity.xCoord, _tileEntity.yCoord, _tileEntity.zCoord);
    }
}
