package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;

/**
 * Interface to implement when defining a ComputerModule.
 */
public interface ComputerModule {
    /**
     * Returns a string representing the module type. It is recommended to user Camel-Case string, with no spaces,
     * starting with a capital letter, i.e. "SampleModuleDiggingHoles".
     * @return Module type.
     */
    public String getModuleType();

    /**
     * Returns a function with the specified name. To make the implementation easier, you can subclass
     * JavaFunctionExecutable class, that defines an easy to use interface for Java developers.
     * @param name Name of the function this module supports.
     * @return Function that will be executed, when invoked by the program, or null if there is no function with this
     * name.
     */
    public FunctionExecutable getFunctionByName(String name);
}
