package tga.Mechanic;

import net.minecraft.item.ItemStack;

public interface IItemChecker {
    boolean HaveEnough(ItemStack stack);
    boolean HaveAll(ItemStack[] stacks);
}