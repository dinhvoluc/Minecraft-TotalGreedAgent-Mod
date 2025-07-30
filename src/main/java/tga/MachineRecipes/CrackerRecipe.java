package tga.MachineRecipes;

import net.minecraft.item.ItemStack;

public class CrackerRecipe {
    public final ItemStack[] ItemInput;
    public final ItemStack ItemOutput;
    public final int EnergyCost;
    public final int RequireMachineLevel;

    public CrackerRecipe(int energyCost, int requireMachineLevel, ItemStack[] itemInput, ItemStack itemOutput) {
        ItemInput = itemInput;
        ItemOutput = itemOutput;
        EnergyCost = energyCost;
        RequireMachineLevel = requireMachineLevel;
    }
}